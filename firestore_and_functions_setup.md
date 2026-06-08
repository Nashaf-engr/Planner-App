# UniTask Notes Backend Deliverables

This document contains the production-ready designs for Firestore, Security Rules, and Cloud Functions corresponding to the core UniTask Notes Android frontend.

---

## 1. Cloud Firestore Schema

To enable multi-device real-time synchronization, configure your Cloud Firestore database with the following nested collection paths:

### Users Collection
* Path: `/users/{userId}`
* Schema:
```json
{
  "userId": "STRING (Primary Key, unique per user auth profile)",
  "name": "STRING (Display Name)",
  "email": "STRING (University Email)",
  "remindersEnabled": "BOOLEAN (Default: true)"
}
```

### Subjects Collection
* Path: `/subjects/{subjectId}`
* Schema:
```json
{
  "subjectId": "STRING (UUID)",
  "userId": "STRING (User Foreign Key mapping user owner)",
  "subjectName": "STRING (e.g. Programming Fundamentals)",
  "subjectCode": "STRING (e.g. CO1010)",
  "color": "STRING (hex code like #3B82F6)",
  "lastUpdated": "TIMESTAMP"
}
```

### Assessments Collection
* Path: `/assessments/{assessmentId}`
* Schema:
```json
{
  "assessmentId": "STRING (UUID)",
  "userId": "STRING (User Foreign Key)",
  "title": "STRING (e.g. Lab Report)",
  "description": "STRING",
  "subjectId": "STRING (Subject Foreign Key relationship)",
  "dueDate": "STRING (format YYYY-MM-DD)",
  "dueTime": "STRING (format HH:MM)",
  "lastUpdated": "TIMESTAMP"
}
```

### Notes Collection
* Path: `/notes/{noteId}`
* Schema:
```json
{
  "noteId": "STRING (UUID)",
  "userId": "STRING (User Foreign Key)",
  "title": "STRING",
  "content": "STRING",
  "lastUpdated": "TIMESTAMP"
}
```

---

## 2. Cloud Firestore Security Rules

These declarative security rules restrict access so that users can **only read, write, or delete records that belong to them** (based on their certified authenticated UID). No student will ever see another student's subjects, notes, or deadlines.

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Helper function checking authentication status
    function isAuthenticated() {
      return request.auth != null;
    }

    // Helper function checking if owner matches authenticated UID
    function isOwner(userId) {
      return isAuthenticated() && request.auth.uid == userId;
    }

    // Users Collection rules
    match /users/{userId} {
      allow read, write: if isOwner(userId);
    }

    // Subjects Collection rules
    match /subjects/{subjectId} {
      allow read, write: if isAuthenticated() && (resource == null || resource.data.userId == request.auth.uid) && (request.resource == null || request.resource.data.userId == request.auth.uid);
    }

    // Assessments Collection rules
    match /assessments/{assessmentId} {
      allow read, write: if isAuthenticated() && (resource == null || resource.data.userId == request.auth.uid) && (request.resource == null || request.resource.data.userId == request.auth.uid);
    }

    // Notes Collection rules
    match /notes/{noteId} {
      allow read, write: if isAuthenticated() && (resource == null || resource.data.userId == request.auth.uid) && (request.resource == null || request.resource.data.userId == request.auth.uid);
    }
  }
}
```

---

## 3. Email Reminder Cloud Function (Node.js)

Configure this Firebase Cloud Function to trigger daily via the `pubsub` Google Cloud scheduler. It audits active assessments, checks user subscription opt-ins, parses course associations, and sends automated reminder templates.

```javascript
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

admin.initializeApp();
const db = admin.firestore();

// Nodemailer transport setup (e.g. SMTP Gmail or modern SES)
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: 'your-university-reminder-system@gmail.com',
    pass: 'YOUR-SECURE-APP-PASSWORD'
  }
});

/**
 * Runs once daily at 08:00 AM UTC to dispatch assessment emails
 */
exports.sendDailyAssessmentReminders = functions.pubsub
  .schedule('0 8 * * *')
  .timeZone('UTC')
  .onRun(async (context) => {
    console.log('Starting Daily Assessment Reminder Dispatch...');
    
    try {
      // 1. Fetch all users who have toggled reminders ON
      const usersSnap = await db.collection('users')
        .where('remindersEnabled', '==', true)
        .get();

      if (usersSnap.empty) {
        console.log('No active user accounts found with reminders enabled.');
        return null;
      }

      const today = new Date();
      today.setHours(0,0,0,0);

      // 2. Loop through users and gather their specific subjects and assessments
      for (const userDoc of usersSnap.docs) {
        const userData = userDoc.data();
        const userId = userDoc.id;
        const userEmail = userData.email;

        // Fetch subjects associated with user to map codes
        const subjectsSnap = await db.collection('subjects')
          .where('userId', '==', userId)
          .get();
        
        const subjectMap = {};
        subjectsSnap.forEach(doc => {
          subjectMap[doc.id] = doc.data();
        });

        // Fetch assessments due
        const assessmentsSnap = await db.collection('assessments')
          .where('userId', '==', userId)
          .get();

        if (assessmentsSnap.empty) {
          continue; // No assessments scheduled, skip email dispatch
        }

        let emailContent = "";

        assessmentsSnap.forEach(doc => {
          const assessment = doc.data();
          const subId = assessment.subjectId;
          const subject = subjectMap[subId] || { subjectName: "Unknown Subject", subjectCode: "N/A" };

          // Parse deadline
          const dueParts = assessment.dueDate.split('-');
          const dueDateObj = new Date(parseInt(dueParts[0]), parseInt(dueParts[1]) - 1, parseInt(dueParts[2]));
          dueDateObj.setHours(0,0,0,0);

          const timeDiff = dueDateObj.getTime() - today.getTime();
          const daysRemaining = Math.ceil(timeDiff / (1000 * 3600 * 24));

          // Calculate friendly visual status
          let remainingDaysText = "";
          if (daysRemaining < 0) {
            remainingDaysText = `OVERDUE by ${Math.abs(daysRemaining)} days`;
          } else if (daysRemaining === 0) {
            remainingDaysText = "DUE TODAY";
          } else if (daysRemaining === 1) {
            remainingDaysText = "DUE TOMORROW";
          } else {
            remainingDaysText = `${daysRemaining} days`;
          }

          emailContent += `
Assessment: ${assessment.title}
Subject: ${subject.subjectName}
Course code: ${subject.subjectCode} 
Deadline: ${assessment.dueDate} at ${assessment.dueTime}
Remaining days: ${remainingDaysText}
--------------------------------------------------\n`;
        });

        // 3. Compile final email message and send using nodemailer transport
        if (emailContent.trim().length > 0) {
          const mailOptions = {
            from: '"UniTask Notes Reminders" <no-reply@unitasknotes.com>',
            to: userEmail,
            subject: 'Assessment Reminder',
            text: `Hello ${userData.name},\n\nHere is your daily digest of upcoming academic deadlines:\n\n${emailContent}\n\nKeep up the excellent work!\nUniTask Notes Server Bot`
          };

          await mailTransport.sendMail(mailOptions);
          console.log(`Dispatched reminder email successfully to subscription user: ${userEmail}`);
        }
      }
    } catch (error) {
      console.error('Failure occurring during cloud reminders execution:', error);
    }

    return null;
  });
