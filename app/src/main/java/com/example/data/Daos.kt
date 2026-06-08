package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    fun observeUserById(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects WHERE userId = :userId ORDER BY subjectName ASC")
    fun observeSubjectsByUser(userId: String): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE userId = :userId ORDER BY subjectName ASC")
    suspend fun getSubjectsByUser(userId: String): List<Subject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Query("DELETE FROM subjects WHERE userId = :userId")
    suspend fun clearSubjectsByUser(userId: String)
}

@Dao
interface AssessmentDao {
    @Query("SELECT * FROM assessments WHERE userId = :userId ORDER BY dueDate ASC, dueTime ASC")
    fun observeAssessmentsByUser(userId: String): Flow<List<Assessment>>

    @Query("SELECT * FROM assessments WHERE userId = :userId ORDER BY dueDate ASC, dueTime ASC")
    suspend fun getAssessmentsByUser(userId: String): List<Assessment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: Assessment)

    @Delete
    suspend fun deleteAssessment(assessment: Assessment)

    @Query("DELETE FROM assessments WHERE userId = :userId")
    suspend fun clearAssessmentsByUser(userId: String)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY lastUpdated DESC")
    fun observeNotesByUser(userId: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY lastUpdated DESC")
    suspend fun getNotesByUser(userId: String): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE userId = :userId")
    suspend fun clearNotesByUser(userId: String)
}
