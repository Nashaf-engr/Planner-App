package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.UUID

class UniTaskRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val subjectDao = database.subjectDao()
    private val assessmentDao = database.assessmentDao()
    private val noteDao = database.noteDao()

    // --- User operations ---
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    suspend fun getUserById(userId: String): User? = userDao.getUserById(userId)
    fun observeUserById(userId: String): Flow<User?> = userDao.observeUserById(userId)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun deleteUser(userId: String) {
        userDao.deleteUserById(userId)
        subjectDao.clearSubjectsByUser(userId)
        assessmentDao.clearAssessmentsByUser(userId)
        noteDao.clearNotesByUser(userId)
    }

    // --- Subject operations ---
    fun observeSubjects(userId: String): Flow<List<Subject>> = subjectDao.observeSubjectsByUser(userId)
    suspend fun getSubjects(userId: String): List<Subject> = subjectDao.getSubjectsByUser(userId)
    suspend fun saveSubject(subject: Subject) = subjectDao.insertSubject(subject)
    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    // --- Assessment operations ---
    fun observeAssessments(userId: String): Flow<List<Assessment>> = assessmentDao.observeAssessmentsByUser(userId)
    suspend fun getAssessments(userId: String): List<Assessment> = assessmentDao.getAssessmentsByUser(userId)
    suspend fun saveAssessment(assessment: Assessment) = assessmentDao.insertAssessment(assessment)
    suspend fun deleteAssessment(assessment: Assessment) = assessmentDao.deleteAssessment(assessment)

    // --- Note operations ---
    fun observeNotes(userId: String): Flow<List<Note>> = noteDao.observeNotesByUser(userId)
    suspend fun getNotes(userId: String): List<Note> = noteDao.getNotesByUser(userId)
    suspend fun saveNote(note: Note) = noteDao.insertNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    // --- Simulated Firebase sync trigger ---
    suspend fun simulateFileSync() {
        // Here we simulate the Firestore real-time listener syncing
        kotlinx.coroutines.delay(1000)
    }
}
