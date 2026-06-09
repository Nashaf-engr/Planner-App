package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainAppContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.UniTaskViewModel
import com.example.notification.TaskReminderHelper

class MainActivity : ComponentActivity() {
    private var mainViewModel: UniTaskViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Notification Channels and cycle
        TaskReminderHelper.createNotificationChannel(this)
        TaskReminderHelper.scheduleHourlyAlarm(this)

        // Request notification permission if Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                val requestPermissionLauncher = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { _: Boolean -> }
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        enableEdgeToEdge()
        setContent {
            val viewModel: UniTaskViewModel = viewModel()
            mainViewModel = viewModel

            // Handle coming from a task notification click (Cold Start)
            val openTasks = intent.getBooleanExtra("open_tasks", false)
            if (openTasks) {
                viewModel.setScreen(AppScreen.ASSESSMENTS)
                intent.removeExtra("open_tasks")
            }

            val isDarkTheme by viewModel.isDarkMode.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppContainer(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Handle coming from a task notification click (Warm Start)
        val openTasks = intent.getBooleanExtra("open_tasks", false)
        if (openTasks) {
            mainViewModel?.setScreen(AppScreen.ASSESSMENTS)
            intent.removeExtra("open_tasks")
        }
    }
}
