package com.mardillu.malami

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mardillu.malami.data.PreferencesManager
import com.mardillu.malami.ui.navigation.MalamiNavHost
import com.mardillu.malami.ui.theme.MalamiTheme
import com.mardillu.malami.work.DailyReadingReminderWorker
import com.mardillu.player_service.service.AudioPlayerService
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefManager: PreferencesManager

    private var isServiceRunning = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MalamiTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MalamiNavHost(isLoggedIn = prefManager.isLoggedIn, ::startService)

                    LaunchedEffect(Unit) {
                        checkAndRequestNotificationPermission()
                    }
                }
            }
        }

        scheduleDailyNotification()
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleDailyNotification()
        } else {
            // Permission denied, they are on their own
        }
    }

    private fun scheduleDailyNotification() {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        // Set the notification time to 8 AM (for example)
        dueDate.set(Calendar.HOUR_OF_DAY, 17)
        dueDate.set(Calendar.MINUTE, 8)
        dueDate.set(Calendar.SECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.DAY_OF_MONTH, 1)
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReadingReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyNotificationWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, AudioPlayerService::class.java))
        isServiceRunning = false
    }

    private fun startService() {
        if (!isServiceRunning) {
            val intent = Intent(this, AudioPlayerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isServiceRunning = true
        }
    }
}

