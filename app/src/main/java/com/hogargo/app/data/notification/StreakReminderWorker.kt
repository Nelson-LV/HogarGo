package com.hogargo.app.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.hogargo.app.data.local.HogarGoDatabase
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Runs once a day in the evening. If the user has an active streak but hasn't
 * completed a task today, shows the "streak at risk" notification.
 */
class StreakReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val tasks = HogarGoDatabase.getInstance(applicationContext).taskDao().getAllTasks().first()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val yesterday = dateFormat.format(
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time,
        )

        val completedDates = tasks.filter { it.completed }.mapNotNull { it.completedDate }.toSet()
        // Same rule as AppViewModel: streak is alive only if the last completion was today or yesterday.
        val streakAlive = completedDates.any { it == yesterday }
        if (streakAlive && today !in completedDates) {
            StreakNotificationHelper.showStreakWarningNotification(applicationContext, completedDates.size)
        }
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "streak_reminder"
        private const val REMINDER_HOUR = 20

        fun schedule(context: Context) {
            val now = Calendar.getInstance()
            val next = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
            }
            val request = PeriodicWorkRequestBuilder<StreakReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(next.timeInMillis - now.timeInMillis, TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}
