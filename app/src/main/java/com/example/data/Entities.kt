package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val passwordHash: String = "", // Used for local authentication
    val remindersEnabled: Boolean = true,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey val subjectId: String,
    val userId: String,
    val subjectName: String,
    val subjectCode: String,
    val color: String, // Hex string like "#3B82F6"
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "assessments")
data class Assessment(
    @PrimaryKey val assessmentId: String,
    val userId: String,
    val title: String,
    val description: String,
    val subjectId: String, // Relationship
    val dueDate: String, // Format: YYYY-MM-DD
    val dueTime: String, // Format: HH:MM
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val noteId: String,
    val userId: String,
    val title: String,
    val content: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
