package com.hogargo.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.hogargo.app.data.notification.StreakNotificationHelper
import com.hogargo.app.data.notification.StreakReminderWorker
import com.hogargo.app.ui.navigation.HogarGoApp
import com.hogargo.app.ui.theme.HogarGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        StreakNotificationHelper.createNotificationChannel(this)
        StreakReminderWorker.schedule(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        setContent {
            HogarGoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HogarGoApp()
                }
            }
        }
    }
}
