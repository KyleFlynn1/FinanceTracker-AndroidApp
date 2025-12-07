package com.example.financetracker.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.financetracker.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.example.financetracker.FinanceTrackerApplication

private const val TAG = "FinanceWorker"

// Worker class for the notification
class FinanceWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    // Do work function to be called by the WorkManager
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get user id from input data
            val userId = inputData.getInt("USER_ID", -1)
            if (userId == -1) return@withContext Result.failure()

            // Calculate today's total expenses
            val todayTotal = calculateTodayTotal(userId)
            showNotification(todayTotal)
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }

    // Get today's total expenses for a user
    private suspend fun calculateTodayTotal(userId : Int): Double {
        android.util.Log.d(TAG, "Starting calculation for userId: $userId")

        val container = (applicationContext as FinanceTrackerApplication).container
        val repository = container.transactionsRepository

        val total = repository.getTodayTotalExpenses(userId).first() ?: 0.0
        android.util.Log.d(TAG, "Today's total for user $userId: $total")

        return total
    }

    // Show a notification with the total expenses for today
    private fun showNotification(total: Double) {
        android.util.Log.d(TAG, "Attempting to show notification for total: $total")
        createNotificationChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Daily Summary")
            .setContentText("You spent $${String.format("%.2f", total)} today")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        if(ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
            android.util.Log.d(TAG, "Notification sent successfully")
        } else {
            android.util.Log.e(TAG, "POST_NOTIFICATIONS permission NOT granted")
        }
    }

    // Create a notification channel for the notification
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily summary of transactions"
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Companion object to hold constants
    companion object {
        private const val CHANNEL_ID = "daily_summary_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
