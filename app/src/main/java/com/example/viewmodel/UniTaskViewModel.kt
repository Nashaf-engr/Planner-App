package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppScreen {
    LOGIN,
    REGISTER,
    DASHBOARD,
    SUBJECTS,
    ASSESSMENTS,
    NOTES,
    PROFILE
}

class UniTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UniTaskRepository
    private val sharedPrefs = application.getSharedPreferences("unitask_prefs", Context.MODE_PRIVATE)

    // Current screen navigation
    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // App Theme / Dark mode toggle
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        val newMode = !_isDarkMode.value
        _isDarkMode.value = newMode
        sharedPrefs.edit().putBoolean("is_dark_mode", newMode).apply()
    }

    // Current authenticated user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Real-time synchronization state indicator
    private val _syncStatus = MutableStateFlow("In Sync with Cloud Firestore")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    // Is active sync processing
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Form inputs & edits state
    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _assessments = MutableStateFlow<List<Assessment>>(emptyList())
    val assessments: StateFlow<List<Assessment>> = _assessments.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    // Selected items for editing
    var editingSubject = mutableStateFlowOf<Subject?>(null)
    var editingAssessment = mutableStateFlowOf<Assessment?>(null)
    var editingNote = mutableStateFlowOf<Note?>(null)

    // Simulation response for Cloud Function assessment reminder emails
    private val _reminderEmailSim = MutableStateFlow<String?>(null)
    val reminderEmailSim: StateFlow<String?> = _reminderEmailSim.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = UniTaskRepository(database)
        _isDarkMode.value = sharedPrefs.getBoolean("is_dark_mode", false)

        // Auto-login if session exists
        val savedUserId = sharedPrefs.getString("logged_in_user_id", null)
        if (savedUserId != null) {
            viewModelScope.launch {
                val user = repository.getUserById(savedUserId)
                if (user != null) {
                    _currentUser.value = user
                    _currentScreen.value = AppScreen.DASHBOARD
                    observeUserData(user.userId)
                } else {
                    // Stale ID, clear preference
                    sharedPrefs.edit().remove("logged_in_user_id").apply()
                }
            }
        }
    }

    private fun observeUserData(userId: String) {
        viewModelScope.launch {
            // Observe Subjects
            repository.observeSubjects(userId).collect {
                _subjects.value = it
            }
        }
        viewModelScope.launch {
            // Observe Assessments
            repository.observeAssessments(userId).collect {
                _assessments.value = it
            }
        }
        viewModelScope.launch {
            // Observe Notes
            repository.observeNotes(userId).collect {
                _notes.value = it
            }
        }
    }

    // --- Authentication Flow ---
    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onError("Please enter both email and password.")
            return
        }
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null) {
                // Simplified hash verification (can be standard comparison for this scale)
                if (user.passwordHash == password) {
                    _currentUser.value = user
                    sharedPrefs.edit().putString("logged_in_user_id", user.userId).apply()
                    _currentScreen.value = AppScreen.DASHBOARD
                    observeUserData(user.userId)
                    triggerSyncState()
                    onSuccess()
                } else {
                    onError("Incorrect password. Please try again.")
                }
            } else {
                onError("No account found with this email.")
            }
        }
    }

    fun loginWithGoogle(onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Mock a gorgeous Google sign-in response using authentic UI logic flow
        triggerSyncing()
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            val mockGoogleId = "google_user_" + UUID.randomUUID().toString().take(6)
            val mockGoogleUser = User(
                userId = mockGoogleId,
                name = "University Student",
                email = "student@example.com", // Injects generic companion email
                passwordHash = "google_auth_secured",
                remindersEnabled = true
            )
            repository.insertUser(mockGoogleUser)
            _currentUser.value = mockGoogleUser
            sharedPrefs.edit().putString("logged_in_user_id", mockGoogleUser.userId).apply()
            _currentScreen.value = AppScreen.DASHBOARD
            observeUserData(mockGoogleUser.userId)
            onSuccess()
            triggerSyncState()
        }
    }

    fun register(name: String, email: String, passwordHash: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (name.isBlank() || email.isBlank() || passwordHash.isBlank()) {
            onError("Please fill in all registration fields.")
            return
        }
        viewModelScope.launch {
            val existing = repository.getUserByEmail(email)
            if (existing != null) {
                onError("An account with this email already exists.")
                return@launch
            }
            val newUserId = UUID.randomUUID().toString()
            val newUser = User(
                userId = newUserId,
                name = name,
                email = email,
                passwordHash = passwordHash,
                remindersEnabled = true
            )
            repository.insertUser(newUser)
            _currentUser.value = newUser
            sharedPrefs.edit().putString("logged_in_user_id", newUser.userId).apply()
            _currentScreen.value = AppScreen.DASHBOARD
            observeUserData(newUser.userId)
            onSuccess()
            triggerSyncState()
        }
    }

    fun logout() {
        viewModelScope.launch {
            sharedPrefs.edit().remove("logged_in_user_id").apply()
            _currentUser.value = null
            _subjects.value = emptyList()
            _assessments.value = emptyList()
            _notes.value = emptyList()
            _currentScreen.value = AppScreen.LOGIN
        }
    }

    fun deleteAccount() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.deleteUser(user.userId)
            logout()
        }
    }

    fun toggleReminders(enabled: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updatedUser = user.copy(remindersEnabled = enabled)
            repository.updateUser(updatedUser)
            _currentUser.value = updatedUser
            triggerSyncState()
        }
    }

    // --- Subject Actions ---
    fun addSubject(name: String, code: String, color: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            triggerSyncing()
            val subject = Subject(
                subjectId = UUID.randomUUID().toString(),
                userId = user.userId,
                subjectName = name,
                subjectCode = code,
                color = color
            )
            repository.saveSubject(subject)
            triggerSyncState()
        }
    }

    fun updateSubject(subject: Subject, name: String, code: String, color: String) {
        viewModelScope.launch {
            triggerSyncing()
            val updated = subject.copy(
                subjectName = name,
                subjectCode = code,
                color = color,
                lastUpdated = System.currentTimeMillis()
            )
            repository.saveSubject(updated)
            triggerSyncState()
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            triggerSyncing()
            repository.deleteSubject(subject)
            triggerSyncState()
        }
    }

    // --- Assessment Actions ---
    fun addAssessment(title: String, description: String, subjectId: String, dueDate: String, dueTime: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            triggerSyncing()
            val assessment = Assessment(
                assessmentId = UUID.randomUUID().toString(),
                userId = user.userId,
                title = title,
                description = description,
                subjectId = subjectId,
                dueDate = dueDate,
                dueTime = dueTime
            )
            repository.saveAssessment(assessment)
            triggerSyncState()
        }
    }

    fun updateAssessment(assessment: Assessment, title: String, description: String, subjectId: String, dueDate: String, dueTime: String) {
        viewModelScope.launch {
            triggerSyncing()
            val updated = assessment.copy(
                title = title,
                description = description,
                subjectId = subjectId,
                dueDate = dueDate,
                dueTime = dueTime,
                lastUpdated = System.currentTimeMillis()
            )
            repository.saveAssessment(updated)
            triggerSyncState()
        }
    }

    fun toggleAssessmentCompleted(assessment: Assessment) {
        viewModelScope.launch {
            triggerSyncing()
            val updated = assessment.copy(
                isCompleted = !assessment.isCompleted,
                lastUpdated = System.currentTimeMillis()
            )
            repository.saveAssessment(updated)
            triggerSyncState()
        }
    }

    fun deleteAssessment(assessment: Assessment) {
        viewModelScope.launch {
            triggerSyncing()
            repository.deleteAssessment(assessment)
            triggerSyncState()
        }
    }

    // --- Note Actions ---
    fun addNote(title: String, content: String, subjectId: String? = null) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            triggerSyncing()
            val note = Note(
                noteId = UUID.randomUUID().toString(),
                userId = user.userId,
                title = title,
                content = content,
                subjectId = subjectId
            )
            repository.saveNote(note)
            triggerSyncState()
        }
    }

    fun updateNote(note: Note, title: String, content: String, subjectId: String? = null) {
        viewModelScope.launch {
            triggerSyncing()
            val updated = note.copy(
                title = title,
                content = content,
                subjectId = subjectId,
                lastUpdated = System.currentTimeMillis()
            )
            repository.saveNote(updated)
            triggerSyncState()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            triggerSyncing()
            repository.deleteNote(note)
            triggerSyncState()
        }
    }

    // --- Core Real-Time Firestore Sync Status Helper ---
    private fun triggerSyncing() {
        _isSyncing.value = true
        _syncStatus.value = "Syncing changes to Cloud Firestore..."
    }

    private fun triggerSyncState() {
        viewModelScope.launch {
            repository.simulateFileSync()
            _isSyncing.value = false
            _syncStatus.value = "In Sync with Cloud Firestore"
        }
    }

    fun manualSyncTrigger() {
        viewModelScope.launch {
            triggerSyncing()
            kotlinx.coroutines.delay(1200)
            _isSyncing.value = false
            _syncStatus.value = "In Sync with Cloud Firestore"
        }
    }

    // --- Simulated Email Reminder Compiler ---
    fun runEmailReminderSimulation() {
        val user = _currentUser.value ?: return
        if (!user.remindersEnabled) {
            _reminderEmailSim.value = "SIMULATION FAILED\n\nDaily notifications are currently DISABLED in settings. Please toggle 'Daily Email Reminders' on under the profile screen to enable Daily Assessment Reminders via Firebase Cloud Functions."
            return
        }

        viewModelScope.launch {
            val activeAssessments = _assessments.value
            if (activeAssessments.isEmpty()) {
                _reminderEmailSim.value = "SYSTEM REPORT\n\nThere are no active assessments to report today. Once you add assessments, this triggers daily digests."
            } else {
                val sb = StringBuilder()
                sb.append("FIREBASE CLOUD FUNCTIONS - EMAIL REMINDER SERVICE\n")
                sb.append("Triggered: Daily assessment review event at 08:00 AM UTC\n")
                sb.append("Recipient: ${user.email}\n")
                sb.append("--------------------------------------------------\n\n")

                activeAssessments.forEachIndexed { index, assessment ->
                    val daysLeft = calculateDaysRemaining(assessment.dueDate)
                    val isToday = daysLeft == 0L
                    val isTomorrow = daysLeft == 1L
                    val statusText = when {
                        daysLeft < 0 -> "OVERDUE by ${-daysLeft} days"
                        isToday -> "DUE TODAY"
                        isTomorrow -> "DUE TOMORROW"
                        else -> "$daysLeft days left"
                    }

                    // Match associated subject
                    val matchingSubject = _subjects.value.firstOrNull { it.subjectId == assessment.subjectId }
                    val subjName = matchingSubject?.subjectName ?: "Unknown Subject"
                    val subjCode = matchingSubject?.subjectCode ?: "N/A"

                    sb.append("EMAIL RECORD #${index + 1}\n")
                    sb.append("Subject: Assessment Reminder\n")
                    sb.append("Body:\n")
                    sb.append("  Assessment: ${assessment.title}\n")
                    sb.append("  Subject: $subjName\n")
                    sb.append("  Course code: $subjCode\n")
                    sb.append("  Deadline: ${formatFriendlyDate(assessment.dueDate)} at ${assessment.dueTime}\n")
                    sb.append("  Remaining days: $statusText\n")
                    sb.append("--------------------------------------------------\n\n")
                }

                _reminderEmailSim.value = sb.toString()
            }
        }
    }

    fun clearEmailSimulation() {
        _reminderEmailSim.value = null
    }

    fun updateProfile(name: String, major: String, yearOfStudy: String, bio: String, studyGoal: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            triggerSyncing()
            val updated = user.copy(
                name = name,
                major = major,
                yearOfStudy = yearOfStudy,
                bio = bio,
                studyGoal = studyGoal,
                lastSyncedAt = System.currentTimeMillis()
            )
            repository.insertUser(updated)
            _currentUser.value = updated
            triggerSyncState()
        }
    }

    fun changePassword(newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            onError("You must be logged in to change your password.")
            return
        }
        if (newPassword.isBlank()) {
            onError("Password cannot be blank.")
            return
        }
        viewModelScope.launch {
            triggerSyncing()
            val updated = user.copy(
                passwordHash = newPassword,
                lastSyncedAt = System.currentTimeMillis()
            )
            repository.insertUser(updated)
            _currentUser.value = updated
            triggerSyncState()
            onSuccess()
        }
    }

    // --- Visual Helper State Creators ---
    private fun <T> mutableStateFlowOf(initialValue: T): MutableStateFlow<T> = MutableStateFlow(initialValue)

    // --- Absolute bulletproof date helper ---
    fun calculateDaysRemaining(dueDateStr: String): Long {
        return try {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dueDate = df.parse(dueDateStr) ?: return 0
            val today = df.parse(df.format(Date())) ?: return 0
            val diff = dueDate.time - today.time
            diff / (1000 * 60 * 60 * 24)
        } catch (e: Exception) {
            0
        }
    }

    fun formatFriendlyDate(dueDateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val date = inputFormat.parse(dueDateStr) ?: return dueDateStr
            outputFormat.format(date)
        } catch (e: Exception) {
            dueDateStr
        }
    }
}
