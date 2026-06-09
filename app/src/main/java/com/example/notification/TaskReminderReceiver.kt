package com.example.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TaskReminderHelper.ACTION_REMIND) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val sharedPrefs = context.getSharedPreferences("unitask_prefs", Context.MODE_PRIVATE)
                    val loggedInUserId = sharedPrefs.getString("logged_in_user_id", null)
                    if (loggedInUserId != null) {
                        val db = AppDatabase.getDatabase(context)
                        val assessments = db.assessmentDao().getAssessmentsByUser(loggedInUserId)
                        val uncompleted = assessments.filter { !it.isCompleted }

                        if (uncompleted.isNotEmpty()) {
                            val randomTask = uncompleted.random()
                            
                            // Find and match the associated subject
                            val subjects = db.subjectDao().getSubjectsByUser(loggedInUserId)
                            val matchingSubject = subjects.firstOrNull { it.subjectId == randomTask.subjectId }
                            val subjectLabel = if (matchingSubject != null) {
                                " [${matchingSubject.subjectCode} - ${matchingSubject.subjectName}]"
                            } else {
                                ""
                            }

                            // Create intent to launch and open MainActivity to the Tasks/Assessments screen
                            val clickIntent = Intent(context, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                putExtra("open_tasks", true)
                            }
                            val pendingIntent = PendingIntent.getActivity(
                                context,
                                randomTask.assessmentId.hashCode(),
                                clickIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )

                            val builder = NotificationCompat.Builder(context, TaskReminderHelper.CHANNEL_ID)
                                .setSmallIcon(android.R.drawable.ic_popup_reminder) // High-contrast system icon
                                .setContentTitle("Task Reminder$subjectLabel")
                                .setContentText("Time is ticking! Don't forget: ${randomTask.title} (Due: ${randomTask.dueDate} ${randomTask.dueTime})")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)

                            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.notify(randomTask.assessmentId.hashCode(), builder.build())
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // Always schedule the next alarm in the hourly cycle
                    TaskReminderHelper.scheduleHourlyAlarm(context)
                    pendingResult.finish()
                }
            }
        }
    }
}
