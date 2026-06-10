package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.delay
import com.example.data.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.UniTaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: UniTaskViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkMode.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle return navigation: go back to Dashboard, then exit on 2nd back tap
    if (currentUser != null && currentScreen != AppScreen.LOGIN && currentScreen != AppScreen.REGISTER && currentScreen != AppScreen.DASHBOARD) {
        BackHandler {
            viewModel.setScreen(AppScreen.DASHBOARD)
        }
    }

    // Top-aligned quick notification presenter (2 seconds with immediate replacement support)
    val showNotification: (String) -> Unit = { msg ->
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val job = launch {
                snackbarHostState.showSnackbar(msg)
            }
            delay(2000)
            job.cancel()
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    Scaffold(
        snackbarHost = {} // Leaving empty to avoid bottom bars/Scaffold blocking
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding(), bottom = 0.dp)
                .drawBehind {
                    // Render premium fluid background linear values
                    val isDark = isDarkTheme
                    val bgBrush = if (isDark) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF030712), // Obsidian deep base
                                Color(0xFF070E22), // Blued space
                                Color(0xFF02040A)  // Dark slate base
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF3F7FD), // Polar liquid ice base
                                Color(0xFFE2EDFC), // Translucent cobalt frosting
                                Color(0xFFF8FAFC)  // Clean white-silver base
                            )
                        )
                    }
                    drawRect(brush = bgBrush)

                    if (isDark) {
                        // Drawing massive radiant liquid cobalt space orbs
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x3A3B82F6), Color.Transparent),
                                center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.25f),
                                radius = size.width * 0.85f
                            ),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.25f),
                            radius = size.width * 0.85f
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x2806B6D4), Color.Transparent),
                                center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.75f),
                                radius = size.width * 0.95f
                            ),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.75f),
                            radius = size.width * 0.95f
                        )
                        // Extra violet neon touch
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x1B8B5CF6), Color.Transparent),
                                center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.5f),
                                radius = size.width * 0.5f
                            ),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.5f),
                            radius = size.width * 0.5f
                        )
                    } else {
                        // Frost glow circles
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x1A3B82F6), Color.Transparent),
                                center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.2f),
                                radius = size.width * 0.75f
                            ),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.2f),
                            radius = size.width * 0.75f
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x1206B6D4), Color.Transparent),
                                center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.8f),
                                radius = size.width * 0.85f
                            ),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.8f),
                            radius = size.width * 0.85f
                        )
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Crossfade(
                    targetState = currentScreen,
                    animationSpec = tween(350),
                    label = "screen_fade"
                ) { screen ->
                    when (screen) {
                        AppScreen.LOGIN -> LoginScreen(
                            viewModel = viewModel,
                            onShowMessage = showNotification
                        )
                        AppScreen.REGISTER -> RegisterScreen(
                            viewModel = viewModel,
                            onShowMessage = showNotification
                        )
                        AppScreen.DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            syncStatus = syncStatus,
                            isSyncing = isSyncing,
                            onShowMessage = showNotification
                        )
                        AppScreen.SUBJECTS -> SubjectsScreen(
                            viewModel = viewModel,
                            onShowMessage = showNotification
                        )
                        AppScreen.ASSESSMENTS -> AssessmentsScreen(
                            viewModel = viewModel,
                            onShowMessage = showNotification
                        )
                        AppScreen.NOTES -> NotesScreen(
                            viewModel = viewModel,
                            onShowMessage = showNotification
                        )
                        AppScreen.PROFILE -> ProfileScreen(
                            viewModel = viewModel,
                            onShowMessage = showNotification
                        )
                    }
                }
            }

            if (currentUser != null && currentScreen != AppScreen.LOGIN && currentScreen != AppScreen.REGISTER) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    UniTaskNavigationBar(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.setScreen(it) }
                    )
                }
            }

            // Beautiful custom top-floating notification toast
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .zIndex(100f)
            ) { snackbarData ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .testTag("app_notification_toast")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "NotificationIcon",
                            tint = MaterialTheme.colorScheme.inverseOnSurface,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = snackbarData.visuals.message,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// --- Bottom Navigation Menu ---
@Composable
fun UniTaskNavigationBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            )
            .drawBehind {
                drawLine(
                    color = Color.White.copy(alpha = 0.15f),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    strokeWidth = 1f
                )
            }
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("app_navigation_bar")
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            val items = listOf(
                NavigationItem("Dashboard", Icons.Default.Home, AppScreen.DASHBOARD),
                NavigationItem("Subjects", Icons.Default.List, AppScreen.SUBJECTS),
                NavigationItem("Tasks", Icons.Default.Check, AppScreen.ASSESSMENTS),
                NavigationItem("Notes", Icons.Default.Edit, AppScreen.NOTES),
                NavigationItem("Profile", Icons.Default.Person, AppScreen.PROFILE)
            )

            items.forEach { item ->
                val selected = currentScreen == item.screen
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.screen) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_${item.label.lowercase()}")
                )
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val screen: AppScreen
)

// --- Sync State Status Indicator Pill ---
@Composable
fun SyncIndicatorRow(
    syncStatus: String,
    isSyncing: Boolean,
    onSyncTrigger: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSyncTrigger() }
            .testTag("sync_status_indicator")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Syncing Done",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = syncStatus,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "Sync Now",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- LOGIN SCREEN ---
@Composable
fun LoginScreen(viewModel: UniTaskViewModel, onShowMessage: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(30.dp))
                // App Logo View
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.ic_launcher_foreground),
                        contentDescription = "UniTask logo",
                        modifier = Modifier
                            .size(100.dp)
                            .testTag("app_logo_image")
                    )
                }
            }

            item {
                Text(
                    text = "UniTask",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Your unified study and assessment master",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("University Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    trailingIcon = {
                        Text(
                            text = if (passwordVisible) "HIDE" else "SHOW",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { passwordVisible = !passwordVisible }
                                .padding(end = 12.dp)
                                .testTag("password_visibility_toggle")
                        )
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Forgot password?",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onShowMessage("Password reset instructions simulated to your email.") }
                            .padding(4.dp)
                            .testTag("forgot_password_link")
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.login(
                            email = email,
                            password = password,
                            onSuccess = { onShowMessage("Welcome back to UniTask!") },
                            onError = { onShowMessage(it) }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("login_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }



            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("New to UniTask? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "Register",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.setScreen(AppScreen.REGISTER) }
                            .padding(4.dp)
                            .testTag("register_link")
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// --- REGISTER SCREEN ---
@Composable
fun RegisterScreen(viewModel: UniTaskViewModel, onShowMessage: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Quick registration to sync assessments and notes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_name_input"),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("University Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_email_input"),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Choose Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_password_input"),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                Button(
                    onClick = {
                        viewModel.register(
                            name = name,
                            email = email,
                            passwordHash = password,
                            onSuccess = { onShowMessage("Welcome, secure account created successfully!") },
                            onError = { onShowMessage(it) }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("register_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Register Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already registered? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "Sign In",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.setScreen(AppScreen.LOGIN) }
                            .padding(4.dp)
                            .testTag("back_to_login_link")
                    )
                }
            }
        }
    }
}

// --- DASHBOARD SCREEN ---
@Composable
fun DashboardScreen(
    viewModel: UniTaskViewModel,
    syncStatus: String,
    isSyncing: Boolean,
    onShowMessage: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var showAcademicDetailsDialog by remember { mutableStateOf(false) }
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val assessments by viewModel.assessments.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    val todayAssessments = remember(assessments) {
        assessments.filter { viewModel.calculateDaysRemaining(it.dueDate) == 0L }
    }
    val upcomingAssessments = remember(assessments) {
        assessments.filter { viewModel.calculateDaysRemaining(it.dueDate) > 0L }
    }
    val mostRecentNote = remember(notes) {
        notes.firstOrNull()
    }

    val todayDateFormatted = remember {
        val sdf = java.text.SimpleDateFormat("EEEE, MMM d", java.util.Locale.getDefault())
        sdf.format(java.util.Date())
    }

    val isSyncingState by viewModel.isSyncing.collectAsStateWithLifecycle()

    PullToRefreshContainer(
        isRefreshing = isSyncingState,
        onRefresh = { viewModel.manualSyncTrigger() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
        // App top Bento-style header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = todayDateFormatted.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hello, ${currentUser?.name ?: "Student"}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                }
                // Initial Circle Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(1.5.dp, Color.White, CircleShape)
                        .clickable { showAcademicDetailsDialog = true }
                        .testTag("dashboard_profile_logo"),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = remember(currentUser) {
                        val name = currentUser?.name ?: "Student"
                        val parts = name.trim().split(" ")
                        if (parts.size >= 2) {
                            "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                        } else {
                            name.take(2).uppercase()
                        }
                    }
                    Text(
                        text = initials,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Sync Pill Status row inside Bento theme
        item {
            SyncIndicatorRow(
                syncStatus = syncStatus,
                isSyncing = isSyncing,
                onSyncTrigger = { viewModel.manualSyncTrigger() }
            )
        }

        // 1. Featured Top Bento Card (TASKS COMPLETION RATE CARD)
        item {
            val totalCount = assessments.size
            val completedCount = assessments.count { it.isCompleted }
            val completedRatio = if (totalCount > 0) completedCount.toFloat() / totalCount else 0.0f
            val percentLabel = "${(completedRatio * 100).toInt()}% COMPLETED"
            val featuredTitle = if (totalCount == 0) "No Tasks Created" else if (completedCount == totalCount) "All Tasks Completed! 🎉" else "Track Your Semester"
            val featuredSubjectText = if (totalCount == 0) {
                "Add your course tasks on the Tasks tab"
            } else {
                "$completedCount of $totalCount academic tasks finalized"
            }

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                border = BorderStroke(1.dp, Color(0xFFBAC8DB)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("featured_bento_banner")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "PERSISTENT TRACKING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                letterSpacing = 1.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Task completion",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = featuredTitle,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 26.sp
                        )
                        Text(
                            text = featuredSubjectText,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Linear Progress Indicator mimicking the Tailwind design
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.4f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(completedRatio)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                             )
                        }
                        Text(
                            text = percentLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // 2. Tasks full-width and Subjects/Notes side-by-side bento layout
        item {
            val nearestTask = remember(assessments) {
                assessments
                    .filter { !it.isCompleted }
                    .minByOrNull { "${it.dueDate} ${it.dueTime}" }
            }
            val nearestTaskSubject = remember(nearestTask, subjects) {
                subjects.firstOrNull { it.subjectId == nearestTask?.subjectId }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tasks_bento_full_width"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Tasks",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                "Tasks",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "VIEW ALL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .clickable { viewModel.setScreen(AppScreen.ASSESSMENTS) }
                                .padding(vertical = 4.dp)
                        )
                    }

                    if (nearestTask != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = nearestTask.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                nearestTaskSubject?.let { sub ->
                                    val sColor = try {
                                        Color(android.graphics.Color.parseColor(sub.color))
                                    } catch (e: Exception) {
                                        MaterialTheme.colorScheme.primary
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(sColor)
                                        )
                                        Text(
                                            text = "${sub.subjectCode} - ${sub.subjectName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            val daysRemaining = viewModel.calculateDaysRemaining(nearestTask.dueDate)
                            val (dayLabel, labelColor) = when {
                                daysRemaining == 0L -> "Today" to MaterialTheme.colorScheme.error
                                daysRemaining == 1L -> "Tomorrow" to MaterialTheme.colorScheme.error
                                daysRemaining > 1L -> "In $daysRemaining days" to MaterialTheme.colorScheme.onSurfaceVariant
                                else -> "Overdue" to MaterialTheme.colorScheme.error
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(labelColor.copy(alpha = 0.1f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = dayLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = labelColor
                                )
                            }
                        }
                    } else {
                        Text(
                            "All tasks completed! 🎉",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // LEFT COLUMN (Subjects Bento Box)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(148.dp)
                        .clickable { viewModel.setScreen(AppScreen.SUBJECTS) }
                        .testTag("subjects_bento_box"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Subjects",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "Subjects",
                                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${subjects.size} Enrolled",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        // Overlapping subject circles
                        if (subjects.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy((-8).dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                subjects.take(4).forEach { s ->
                                    val sColor = try {
                                        Color(android.graphics.Color.parseColor(s.color))
                                    } catch (e: Exception) {
                                        MaterialTheme.colorScheme.primary
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(sColor)
                                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    )
                                }
                            }
                        } else {
                            Text(
                                "No subjects added",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // RIGHT COLUMN (Notes bento link)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(148.dp)
                        .clickable { viewModel.setScreen(AppScreen.NOTES) }
                        .testTag("notes_bento_link"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Notes",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Notes",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            if (mostRecentNote != null) {
                                Text(
                                    text = mostRecentNote.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = mostRecentNote.content,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 14.sp
                                )
                            } else {
                                Text(
                                    "No notes yet. Tap to draft one!",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Go Notes",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Note horizontal spanned banner
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_note_bento_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "QUICK NOTE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        val snip = if (mostRecentNote != null) {
                            "\"${mostRecentNote.title}: ${mostRecentNote.content}\""
                        } else {
                            "\"Review Big O notation for tomorrow...\""
                        }
                        Text(
                            text = snip,
                            fontSize = 13.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Decor star",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(start = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

    if (showAcademicDetailsDialog) {
        Dialog(onDismissRequest = { showAcademicDetailsDialog = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("dashboard_profile_view_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Student Academic Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Name
                    Column {
                        Text(
                            text = "STUDENT NAME",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentUser?.name?.takeIf { it.isNotBlank() } ?: "Not provided",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Major & Year
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MAJOR / COURSE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentUser?.major?.takeIf { it.isNotBlank() } ?: "Not provided",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "YEAR OF STUDY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentUser?.yearOfStudy?.takeIf { it.isNotBlank() } ?: "Not provided",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Bio
                    Column {
                        Text(
                            text = "STUDENT BRIEF BIO / TAGLINE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentUser?.bio?.takeIf { it.isNotBlank() } ?: "Not provided",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Goal
                    Column {
                        Text(
                            text = "PERSONAL STUDY TARGET GOAL",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentUser?.studyGoal?.takeIf { it.isNotBlank() } ?: "Not provided",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showAcademicDetailsDialog = false },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun DashboardAssessmentListItem(
    assessment: Assessment,
    subject: Subject?,
    viewModel: UniTaskViewModel
) {
    val daysLeft = viewModel.calculateDaysRemaining(assessment.dueDate)
    val indicatorColor = when {
        assessment.isCompleted -> Color(0xFF10B981)
        daysLeft < 0 -> Color(0xFFEF4444)
        daysLeft == 0L -> Color(0xFFEF4444)
        daysLeft == 1L -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }
    val daysText = when {
        assessment.isCompleted -> "Completed"
        daysLeft < 0 -> "Overdue"
        daysLeft == 0L -> "Due Today"
        daysLeft == 1L -> "Due Tomorrow"
        else -> "$daysLeft d left"
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                checked = assessment.isCompleted,
                onCheckedChange = { viewModel.toggleAssessmentCompleted(assessment) },
                modifier = Modifier.testTag("dashboard_assessment_checkbox_${assessment.assessmentId.take(5)}")
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val blockColor = subject?.let { Color(android.graphics.Color.parseColor(it.color)) } ?: MaterialTheme.colorScheme.outline
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(blockColor)
                    )
                    Text(
                        text = subject?.subjectCode ?: "N/A",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = assessment.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = if (assessment.isCompleted) MaterialTheme.typography.titleMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Due: ${viewModel.formatFriendlyDate(assessment.dueDate)} at ${assessment.dueTime}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(indicatorColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = daysText,
                    fontSize = 11.sp,
                    color = indicatorColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- SUBJECTS SCREEN ---
@Composable
fun SubjectsScreen(viewModel: UniTaskViewModel, onShowMessage: (String) -> Unit) {
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedSubjectForEdit by remember { mutableStateOf<Subject?>(null) }

    val presetColors = listOf("#3B82F6", "#6366F1", "#10B981", "#EC4899", "#F59E0B", "#EF4444")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_subject_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Subject Management",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            val isSyncingState by viewModel.isSyncing.collectAsStateWithLifecycle()
            PullToRefreshContainer(
                isRefreshing = isSyncingState,
                onRefresh = { viewModel.manualSyncTrigger() },
                modifier = Modifier.weight(1f)
            ) {
                if (subjects.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Text("No subjects found. Create a subject using the button below.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(subjects) { subject ->
                            SubjectCard(
                                subject = subject,
                                onEdit = {
                                    selectedSubjectForEdit = subject
                                    showEditDialog = true
                                },
                                onDelete = {
                                    viewModel.deleteSubject(subject)
                                    onShowMessage("Subject ${subject.subjectCode} deleted.")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var selectedColorHex by remember { mutableStateOf(presetColors[1]) }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("add_subject_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Add New Subject", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Subject Name") },
                        placeholder = { Text("Programming Fundamentals") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("subject_name_field")
                    )
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Subject Code") },
                        placeholder = { Text("CO1010") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("subject_code_field")
                    )
                    Text("Color Accent", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        presetColors.forEach { cHex ->
                            val c = Color(android.graphics.Color.parseColor(cHex))
                            val isSelected = selectedColorHex == cHex
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .clickable { selectedColorHex = cHex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (name.isNotBlank() && code.isNotBlank()) {
                                    viewModel.addSubject(name, code, selectedColorHex)
                                    showAddDialog = false
                                    onShowMessage("Subject $code added successfully.")
                                } else {
                                    onShowMessage("Please complete all subjects fields.")
                                }
                            },
                            modifier = Modifier.testTag("save_subject_button")
                        ) {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && selectedSubjectForEdit != null) {
        val currentSub = selectedSubjectForEdit!!
        var name by remember(currentSub) { mutableStateOf(currentSub.subjectName) }
        var code by remember(currentSub) { mutableStateOf(currentSub.subjectCode) }
        var selectedColorHex by remember(currentSub) { mutableStateOf(currentSub.color) }

        Dialog(onDismissRequest = { showEditDialog = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("edit_subject_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Modify Subject Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Subject Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_subject_name_field")
                    )
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Subject Code") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_subject_code_field")
                    )
                    Text("Color Accent", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        presetColors.forEach { cHex ->
                            val c = Color(android.graphics.Color.parseColor(cHex))
                            val isSelected = selectedColorHex == cHex
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .clickable { selectedColorHex = cHex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (name.isNotBlank() && code.isNotBlank()) {
                                    viewModel.updateSubject(currentSub, name, code, selectedColorHex)
                                    showEditDialog = false
                                    onShowMessage("Subject details updated.")
                                } else {
                                    onShowMessage("Subject code/name cannot be empty.")
                                }
                            },
                            modifier = Modifier.testTag("update_subject_button")
                        ) {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: Subject,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val m3Color = remember(subject.color) {
        try {
            Color(android.graphics.Color.parseColor(subject.color))
        } catch (e: Exception) {
            Color(0xFF6366F1)
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_card_${subject.subjectCode.lowercase()}")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(m3Color)
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = subject.subjectCode,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = m3Color
                )
                Text(
                    text = subject.subjectName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("subject_edit_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("subject_delete_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// --- ASSESSMENTS SCREEN ---
@Composable
fun AssessmentsScreen(viewModel: UniTaskViewModel, onShowMessage: (String) -> Unit) {
    val assessments by viewModel.assessments.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedAssessmentForEdit by remember { mutableStateOf<Assessment?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (subjects.isEmpty()) {
                        onShowMessage("Please add at least one subject first before scheduling assessments.")
                    } else {
                        showAddDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_assessment_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Assessment")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Tasks & Deadlines",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            val isSyncingState by viewModel.isSyncing.collectAsStateWithLifecycle()
            PullToRefreshContainer(
                isRefreshing = isSyncingState,
                onRefresh = { viewModel.manualSyncTrigger() },
                modifier = Modifier.weight(1f)
            ) {
                if (assessments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Text("No tasks found. Create a task using the FAB button below.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(assessments) { assessment ->
                            val matchingSubject = subjects.firstOrNull { it.subjectId == assessment.subjectId }
                            AssessmentCard(
                                assessment = assessment,
                                subject = matchingSubject,
                                viewModel = viewModel,
                                onEdit = {
                                    selectedAssessmentForEdit = assessment
                                    showEditDialog = true
                                },
                                onDelete = {
                                    viewModel.deleteAssessment(assessment)
                                    onShowMessage("Task ${assessment.title} deleted.")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.subjectId ?: "") }
        var dueDateStr by remember { mutableStateOf("") }
        var dueTimeStr by remember { mutableStateOf("") }

        val templates = listOf("Lab Report", "Quiz", "Assignment", "Presentation", "Mid Exam")

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("add_assessment_dialog")
            ) {
                LazyColumn(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Schedule New Assessment", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    item {
                        Text("Autofill Templates", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            templates.take(3).forEach { tmpl ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { title = tmpl }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(tmpl, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Assessment Title") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("assessment_title_field")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("Associate Subject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            subjects.forEach { s ->
                                val selected = s.subjectId == selectedSubjectId
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { selectedSubjectId = s.subjectId }
                                        .padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(s.color)))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(s.subjectName, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }

                     item {
                        val context = LocalContext.current
                        OutlinedTextField(
                            value = dueDateStr,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Due Date") },
                            placeholder = { Text("Tap to select date...") },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    val calendar = java.util.Calendar.getInstance()
                                    val ymd = dueDateStr.split("-")
                                    if (ymd.size == 3) {
                                        try {
                                            calendar.set(java.util.Calendar.YEAR, ymd[0].toInt())
                                            calendar.set(java.util.Calendar.MONTH, ymd[1].toInt() - 1)
                                            calendar.set(java.util.Calendar.DAY_OF_MONTH, ymd[2].toInt())
                                        } catch (_: Exception) {}
                                    }
                                    android.app.DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            dueDateStr = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                                        },
                                        calendar.get(java.util.Calendar.YEAR),
                                        calendar.get(java.util.Calendar.MONTH),
                                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                                    ).show()
                                }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val calendar = java.util.Calendar.getInstance()
                                    val ymd = dueDateStr.split("-")
                                    if (ymd.size == 3) {
                                        try {
                                            calendar.set(java.util.Calendar.YEAR, ymd[0].toInt())
                                            calendar.set(java.util.Calendar.MONTH, ymd[1].toInt() - 1)
                                            calendar.set(java.util.Calendar.DAY_OF_MONTH, ymd[2].toInt())
                                        } catch (_: Exception) {}
                                    }
                                    android.app.DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            dueDateStr = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                                        },
                                        calendar.get(java.util.Calendar.YEAR),
                                        calendar.get(java.util.Calendar.MONTH),
                                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                                .testTag("assessment_date_field")
                        )
                    }

                    item {
                        val context = LocalContext.current
                        OutlinedTextField(
                            value = dueTimeStr,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Due Time") },
                            placeholder = { Text("Tap to select time...") },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    val calendar = java.util.Calendar.getInstance()
                                    val hm = dueTimeStr.split(":")
                                    if (hm.size == 2) {
                                        try {
                                            calendar.set(java.util.Calendar.HOUR_OF_DAY, hm[0].toInt())
                                            calendar.set(java.util.Calendar.MINUTE, hm[1].toInt())
                                        } catch (_: Exception) {}
                                    }
                                    android.app.TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            dueTimeStr = String.format(java.util.Locale.US, "%02d:%02d", hourOfDay, minute)
                                        },
                                        calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                        calendar.get(java.util.Calendar.MINUTE),
                                        true
                                    ).show()
                                }) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Select Time")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val calendar = java.util.Calendar.getInstance()
                                    val hm = dueTimeStr.split(":")
                                    if (hm.size == 2) {
                                        try {
                                            calendar.set(java.util.Calendar.HOUR_OF_DAY, hm[0].toInt())
                                            calendar.set(java.util.Calendar.MINUTE, hm[1].toInt())
                                        } catch (_: Exception) {}
                                    }
                                    android.app.TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            dueTimeStr = String.format(java.util.Locale.US, "%02d:%02d", hourOfDay, minute)
                                        },
                                        calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                        calendar.get(java.util.Calendar.MINUTE),
                                        true
                                    ).show()
                                }
                                .testTag("assessment_time_field")
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                        ) {
                            TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    if (title.isNotBlank() && dueDateStr.isNotBlank() && dueTimeStr.isNotBlank()) {
                                        viewModel.addAssessment(
                                            title = title,
                                            description = description,
                                            subjectId = selectedSubjectId,
                                            dueDate = dueDateStr,
                                            dueTime = dueTimeStr
                                        )
                                        showAddDialog = false
                                        onShowMessage("Assessment task scheduled successfully.")
                                    } else {
                                        onShowMessage("Please specify title, date, and due time.")
                                    }
                                },
                                modifier = Modifier.testTag("save_assessment_button")
                            ) {
                                Text("Schedule")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && selectedAssessmentForEdit != null) {
        val currentAss = selectedAssessmentForEdit!!
        var title by remember(currentAss) { mutableStateOf(currentAss.title) }
        var description by remember(currentAss) { mutableStateOf(currentAss.description) }
        var selectedSubjectId by remember(currentAss) { mutableStateOf(currentAss.subjectId) }
        var dueDateStr by remember(currentAss) { mutableStateOf(currentAss.dueDate) }
        var dueTimeStr by remember(currentAss) { mutableStateOf(currentAss.dueTime) }

        Dialog(onDismissRequest = { showEditDialog = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("edit_assessment_dialog")
            ) {
                LazyColumn(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Update Assessment Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Assessment Title") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_assessment_title_field")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("Associate Subject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            subjects.forEach { s ->
                                val selected = s.subjectId == selectedSubjectId
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { selectedSubjectId = s.subjectId }
                                        .padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(s.color)))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(s.subjectName, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }

                     item {
                        val context = LocalContext.current
                        OutlinedTextField(
                            value = dueDateStr,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Due Date") },
                            placeholder = { Text("Tap to select date...") },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    val calendar = java.util.Calendar.getInstance()
                                    val ymd = dueDateStr.split("-")
                                    if (ymd.size == 3) {
                                        try {
                                            calendar.set(java.util.Calendar.YEAR, ymd[0].toInt())
                                            calendar.set(java.util.Calendar.MONTH, ymd[1].toInt() - 1)
                                            calendar.set(java.util.Calendar.DAY_OF_MONTH, ymd[2].toInt())
                                        } catch (_: Exception) {}
                                    }
                                    android.app.DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            dueDateStr = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                                        },
                                        calendar.get(java.util.Calendar.YEAR),
                                        calendar.get(java.util.Calendar.MONTH),
                                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                                    ).show()
                                }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val calendar = java.util.Calendar.getInstance()
                                    val ymd = dueDateStr.split("-")
                                    if (ymd.size == 3) {
                                        try {
                                            calendar.set(java.util.Calendar.YEAR, ymd[0].toInt())
                                            calendar.set(java.util.Calendar.MONTH, ymd[1].toInt() - 1)
                                            calendar.set(java.util.Calendar.DAY_OF_MONTH, ymd[2].toInt())
                                        } catch (_: Exception) {}
                                    }
                                    android.app.DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            dueDateStr = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                                        },
                                        calendar.get(java.util.Calendar.YEAR),
                                        calendar.get(java.util.Calendar.MONTH),
                                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                                .testTag("edit_assessment_date_field")
                        )
                    }

                    item {
                        val context = LocalContext.current
                        OutlinedTextField(
                            value = dueTimeStr,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Due Time") },
                            placeholder = { Text("Tap to select time...") },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    val calendar = java.util.Calendar.getInstance()
                                    val hm = dueTimeStr.split(":")
                                    if (hm.size == 2) {
                                        try {
                                            calendar.set(java.util.Calendar.HOUR_OF_DAY, hm[0].toInt())
                                            calendar.set(java.util.Calendar.MINUTE, hm[1].toInt())
                                        } catch (_: Exception) {}
                                    }
                                    android.app.TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            dueTimeStr = String.format(java.util.Locale.US, "%02d:%02d", hourOfDay, minute)
                                        },
                                        calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                        calendar.get(java.util.Calendar.MINUTE),
                                        true
                                    ).show()
                                }) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Select Time")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val calendar = java.util.Calendar.getInstance()
                                    val hm = dueTimeStr.split(":")
                                    if (hm.size == 2) {
                                        try {
                                            calendar.set(java.util.Calendar.HOUR_OF_DAY, hm[0].toInt())
                                            calendar.set(java.util.Calendar.MINUTE, hm[1].toInt())
                                        } catch (_: Exception) {}
                                    }
                                    android.app.TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            dueTimeStr = String.format(java.util.Locale.US, "%02d:%02d", hourOfDay, minute)
                                        },
                                        calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                        calendar.get(java.util.Calendar.MINUTE),
                                        true
                                    ).show()
                                }
                                .testTag("edit_assessment_time_field")
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                        ) {
                            TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    if (title.isNotBlank() && dueDateStr.isNotBlank() && dueTimeStr.isNotBlank()) {
                                        viewModel.updateAssessment(
                                            assessment = currentAss,
                                            title = title,
                                            description = description,
                                            subjectId = selectedSubjectId,
                                            dueDate = dueDateStr,
                                            dueTime = dueTimeStr
                                        )
                                        showEditDialog = false
                                        onShowMessage("Assessment details updated successfully.")
                                    } else {
                                        onShowMessage("Please specify required values.")
                                    }
                                },
                                modifier = Modifier.testTag("update_assessment_button")
                            ) {
                                Text("Update")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssessmentCard(
    assessment: Assessment,
    subject: Subject?,
    viewModel: UniTaskViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val daysLeft = viewModel.calculateDaysRemaining(assessment.dueDate)
    val cardColor = when {
        assessment.isCompleted -> Color(0xFF10B981)
        daysLeft < 0 -> Color(0xFFEF4444)
        daysLeft == 0L -> Color(0xFFEF4444)
        daysLeft == 1L -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    val relativeText = when {
        assessment.isCompleted -> "Completed"
        daysLeft < 0 -> "Overdue (${-daysLeft}d)"
        daysLeft == 0L -> "Today"
        daysLeft == 1L -> "Tomorrow"
        else -> "${daysLeft}d left"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("assessment_card_${assessment.title.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = assessment.isCompleted,
                onCheckedChange = { viewModel.toggleAssessmentCompleted(assessment) },
                modifier = Modifier
                    .size(40.dp)
                    .testTag("assessment_card_checkbox_${assessment.assessmentId.take(5)}")
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Topic Subject badge & Title Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (subject != null) {
                        val subColor = try {
                            Color(android.graphics.Color.parseColor(subject.color))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(subColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 1.5.dp)
                        ) {
                            Text(
                                text = subject.subjectCode,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = subColor
                            )
                        }
                    }

                    Text(
                        text = assessment.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = if (assessment.isCompleted) {
                            MaterialTheme.typography.bodyMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                        } else {
                            MaterialTheme.typography.bodyMedium
                        }
                    )
                }

                // Friendly deadline date
                Text(
                    text = "Due: ${viewModel.formatFriendlyDate(assessment.dueDate)} @ ${assessment.dueTime}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                if (assessment.description.isNotBlank()) {
                    Text(
                        text = assessment.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right side state & action controls column
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Due relative status tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(cardColor.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = relativeText.uppercase(),
                        fontSize = 9.sp,
                        color = cardColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Edit & Delete row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("assessment_edit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("assessment_delete_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- NOTES SCREEN ---
@Composable
fun NotesScreen(viewModel: UniTaskViewModel, onShowMessage: (String) -> Unit) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedNoteForEdit by remember { mutableStateOf<Note?>(null) }
    var showViewDialog by remember { mutableStateOf(false) }
    var selectedNoteForView by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_note_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Academic Notes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            val isSyncingState by viewModel.isSyncing.collectAsStateWithLifecycle()
            PullToRefreshContainer(
                isRefreshing = isSyncingState,
                onRefresh = { viewModel.manualSyncTrigger() },
                modifier = Modifier.weight(1f)
            ) {
                if (notes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Text("No notes written yet. Tap the FAB to write notes.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 96.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notes) { note ->
                            val matchingSubject = subjects.firstOrNull { it.subjectId == note.subjectId }
                            NoteGridCard(
                                note = note,
                                subject = matchingSubject,
                                onClick = {
                                    selectedNoteForView = note
                                    showViewDialog = true
                                },
                                onEdit = {
                                    selectedNoteForEdit = note
                                    showEditDialog = true
                                },
                                onDelete = {
                                    viewModel.deleteNote(note)
                                    onShowMessage("Note deleted successfully.")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var selectedSubjectId by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("add_note_dialog")
            ) {
                LazyColumn(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text("Create New Note", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Note Title") },
                            placeholder = { Text("Lab 4 Emojis 📝") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("note_title_field")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Content Notes") },
                            minLines = 4,
                            maxLines = 8,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("note_content_field")
                        )
                    }

                    item {
                        Text("Associate Subject (Optional)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            subjects.forEach { s ->
                                val selected = s.subjectId == selectedSubjectId
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { selectedSubjectId = if (selected) null else s.subjectId }
                                        .padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(s.color)))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(s.subjectName, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                        ) {
                            TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    if (title.isNotBlank()) {
                                        viewModel.addNote(title, content, selectedSubjectId)
                                        showAddDialog = false
                                        onShowMessage("Note saved and synced.")
                                    } else {
                                        onShowMessage("Please specify a note title.")
                                    }
                                },
                                modifier = Modifier.testTag("save_note_button")
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && selectedNoteForEdit != null) {
        val currentNote = selectedNoteForEdit!!
        var title by remember(currentNote) { mutableStateOf(currentNote.title) }
        var content by remember(currentNote) { mutableStateOf(currentNote.content) }
        var selectedSubjectId by remember(currentNote) { mutableStateOf(currentNote.subjectId) }

        Dialog(onDismissRequest = { showEditDialog = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("edit_note_dialog")
            ) {
                LazyColumn(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text("Modify Note", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Note Title") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_note_title_field")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Content Notes") },
                            minLines = 4,
                            maxLines = 8,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_note_content_field")
                        )
                    }

                    item {
                        Text("Associate Subject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            subjects.forEach { s ->
                                val selected = s.subjectId == selectedSubjectId
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { selectedSubjectId = if (selected) null else s.subjectId }
                                        .padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(s.color)))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(s.subjectName, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                        ) {
                            TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
                            Button(
                                onClick = {
                                    if (title.isNotBlank()) {
                                        viewModel.updateNote(currentNote, title, content, selectedSubjectId)
                                        showEditDialog = false
                                        onShowMessage("Note updated details synchronized.")
                                    } else {
                                        onShowMessage("Title can't be empty.")
                                    }
                                },
                                modifier = Modifier.testTag("update_note_button")
                            ) {
                                Text("Update")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showViewDialog && selectedNoteForView != null) {
        val note = selectedNoteForView!!
        val matchingSubject = subjects.firstOrNull { it.subjectId == note.subjectId }
        Dialog(onDismissRequest = { showViewDialog = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("view_note_dialog")
            ) {
                LazyColumn(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "View Note",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                            if (matchingSubject != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(android.graphics.Color.parseColor(matchingSubject.color)).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${matchingSubject.subjectCode} - ${matchingSubject.subjectName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(android.graphics.Color.parseColor(matchingSubject.color)),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }

                    item {
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        val formattedDate = remember(note.lastUpdated) {
                            try {
                                val sdf = java.text.SimpleDateFormat("MMM dd, yyyy - hh:mm a", java.util.Locale.getDefault())
                                sdf.format(java.util.Date(note.lastUpdated))
                            } catch (e: Exception) {
                                "Recently"
                            }
                        }
                        Text(
                            text = "Last updated: $formattedDate",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showViewDialog = false },
                                modifier = Modifier.testTag("close_view_note_button")
                            ) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteGridCard(
    note: Note,
    subject: Subject? = null,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("note_card_${note.title.lowercase().replace(" ", "_")}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (subject != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(android.graphics.Color.parseColor(subject.color)).copy(alpha = 0.15f),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = subject.subjectCode,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor(subject.color)),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                note.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                note.content,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).testTag("note_edit_button")) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("note_delete_button")) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// --- PROFILE SCREEN ---
@Composable
fun ProfileScreen(viewModel: UniTaskViewModel, onShowMessage: (String) -> Unit) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    var showUpdateProfileConfirmation by remember { mutableStateOf(false) }
    var showChangePasswordConfirmation by remember { mutableStateOf(false) }
    var showDeleteAccountConfirmation by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Student Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.name ?: "S").take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                    }
                    Column {
                        Text(
                            currentUser?.name ?: "N/A",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            currentUser?.email ?: "N/A",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = { showEditProfileDialog = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("edit_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Button(
                onClick = { showChangePasswordDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("show_change_password_dialog_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Password", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        item {
            val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().testTag("theme_switch_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Theme",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                "App Theme Mode",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isDark,
                        onCheckedChange = {
                            viewModel.toggleDarkMode()
                            onShowMessage(if (!isDark) "Switched to Dark Cobalt theme!" else "Switched to Light Ice theme!")
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("theme_toggle_switch")
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Notification Testing System",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Test the notification system instantly. Clicking below will send a local notification with matched subject details if you have any uncompleted tasks.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            val intent = android.content.Intent(context, com.example.notification.TaskReminderReceiver::class.java).apply {
                                action = com.example.notification.TaskReminderHelper.ACTION_REMIND
                            }
                            context.sendBroadcast(intent)
                            onShowMessage("Immediate notification simulation triggered!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_instant_notification_button")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Trigger Local Notification Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Risk Actions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showLogoutConfirmation = true
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("logout_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Sign Out", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Button(
                            onClick = {
                                showDeleteAccountConfirmation = true
                            },
                            modifier = Modifier
                                .weight(2.5f)
                                .testTag("delete_account_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Delete Account", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showEditProfileDialog) {
        var tempName by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
        var tempMajor by remember(currentUser) { mutableStateOf(currentUser?.major ?: "") }
        var tempYearOfStudy by remember(currentUser) { mutableStateOf(currentUser?.yearOfStudy ?: "") }
        var tempBio by remember(currentUser) { mutableStateOf(currentUser?.bio ?: "") }
        var tempStudyGoal by remember(currentUser) { mutableStateOf(currentUser?.studyGoal ?: "") }

        Dialog(onDismissRequest = { showEditProfileDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("edit_profile_dialog")
            ) {
                LazyColumn(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Student Academic Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    item {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Student Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_name_field")
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = tempMajor,
                                onValueChange = { tempMajor = it },
                                label = { Text("Major / Course") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_major_field")
                            )
                            OutlinedTextField(
                                value = tempYearOfStudy,
                                onValueChange = { tempYearOfStudy = it },
                                label = { Text("Year of Study") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_year_field")
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = tempBio,
                            onValueChange = { tempBio = it },
                            label = { Text("Student Brief Bio / Tagline") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_bio_field")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = tempStudyGoal,
                            onValueChange = { tempStudyGoal = it },
                            label = { Text("Personal Study Target Goal") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_study_goal_field")
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showEditProfileDialog = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    if (tempName.isNotBlank()) {
                                        showUpdateProfileConfirmation = true
                                    } else {
                                        onShowMessage("Name remains a required field.")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("save_profile_button")
                            ) {
                                Text("Save Changes", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (showUpdateProfileConfirmation) {
            AlertDialog(
                onDismissRequest = { showUpdateProfileConfirmation = false },
                title = { Text("Confirm Update") },
                text = { Text("Are you sure you want to update your student academic details?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateProfile(tempName, tempMajor, tempYearOfStudy, tempBio, tempStudyGoal)
                            onShowMessage("Profile details updated successfully!")
                            showUpdateProfileConfirmation = false
                            showEditProfileDialog = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUpdateProfileConfirmation = false }) {
                        Text("No")
                    }
                }
            )
        }
    }

    if (showChangePasswordDialog) {
        var tempNewPassword by remember { mutableStateOf("") }
        var tempConfirmPassword by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showChangePasswordDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("change_password_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Change Password",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = tempNewPassword,
                        onValueChange = { tempNewPassword = it },
                        label = { Text("New Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_password_field")
                    )

                    OutlinedTextField(
                        value = tempConfirmPassword,
                        onValueChange = { tempConfirmPassword = it },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_password_field")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showChangePasswordDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (tempNewPassword.isBlank()) {
                                    onShowMessage("Please enter a new password.")
                                } else if (tempNewPassword != tempConfirmPassword) {
                                    onShowMessage("Passwords do not match!")
                                } else if (tempNewPassword == (currentUser?.passwordHash ?: "")) {
                                    onShowMessage("Enter new password")
                                } else {
                                    showChangePasswordConfirmation = true
                                }
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("update_password_action_button")
                        ) {
                            Text("Update", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (showChangePasswordConfirmation) {
            AlertDialog(
                onDismissRequest = { showChangePasswordConfirmation = false },
                title = { Text("Confirm Reset Password") },
                text = { Text("Are you sure you want to change your password?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.changePassword(
                                newPassword = tempNewPassword,
                                onSuccess = {
                                    showChangePasswordConfirmation = false
                                    showChangePasswordDialog = false
                                    onShowMessage("Password changed successfully!")
                                },
                                onError = {
                                    showChangePasswordConfirmation = false
                                    onShowMessage(it)
                                }
                            )
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChangePasswordConfirmation = false }) {
                        Text("No")
                    }
                }
            )
        }
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text("Confirm Sign Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        onShowMessage("Logged out successfully.")
                        showLogoutConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text("No")
                }
            }
        )
    }

    if (showDeleteAccountConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountConfirmation = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to permanently delete your account? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount()
                        onShowMessage("Your account has been permanently deleted.")
                        showDeleteAccountConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountConfirmation = false }) {
                    Text("No")
                }
            }
        )
    }
}

// --- GESTURE-BASED PULL-TO-REFRESH CONTAINER ---
@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var dragOffset by remember { mutableStateOf(0f) }
    val maxDrag = 350f
    val refreshThreshold = 220f

    val animatedOffset by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isRefreshing) refreshThreshold else dragOffset,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMedium)
    )

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            dragOffset = 0f
        }
    }

    val nestedScrollConnection = remember {
        object : androidx.compose.ui.input.nestedscroll.NestedScrollConnection {
            override fun onPreScroll(
                available: androidx.compose.ui.geometry.Offset,
                source: androidx.compose.ui.input.nestedscroll.NestedScrollSource
            ): androidx.compose.ui.geometry.Offset {
                return if (available.y < 0 && dragOffset > 0) {
                    val prevOffset = dragOffset
                    dragOffset = (dragOffset + available.y).coerceAtLeast(0f)
                    androidx.compose.ui.geometry.Offset(0f, dragOffset - prevOffset)
                } else {
                    androidx.compose.ui.geometry.Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: androidx.compose.ui.geometry.Offset,
                available: androidx.compose.ui.geometry.Offset,
                source: androidx.compose.ui.input.nestedscroll.NestedScrollSource
            ): androidx.compose.ui.geometry.Offset {
                return if (available.y > 0) {
                    val prevOffset = dragOffset
                    dragOffset = (dragOffset + available.y * 0.45f).coerceAtMost(maxDrag)
                    androidx.compose.ui.geometry.Offset(0f, dragOffset - prevOffset)
                } else {
                    androidx.compose.ui.geometry.Offset.Zero
                }
            }

            override suspend fun onPreFling(available: androidx.compose.ui.unit.Velocity): androidx.compose.ui.unit.Velocity {
                if (dragOffset >= refreshThreshold && !isRefreshing) {
                    onRefresh()
                }
                dragOffset = 0f
                return androidx.compose.ui.unit.Velocity.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = animatedOffset
                }
        ) {
            content()
        }

        if (animatedOffset > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .graphicsLayer {
                        translationY = (animatedOffset - 110f).coerceAtLeast(0f)
                        alpha = (animatedOffset / refreshThreshold).coerceAtMost(1f)
                    },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isRefreshing) "Syncing..." else "Pull more to sync",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

