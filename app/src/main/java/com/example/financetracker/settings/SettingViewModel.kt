package com.example.financetracker.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.financetracker.workers.FinanceWorker
import java.util.concurrent.TimeUnit

class SettingViewModel : ViewModel() {

    fun enableDailySummary(context: Context, userId: Int) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<FinanceWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInputData(workDataOf("USER_ID" to userId))
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "daily_notification",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
            )
    }

    fun disableDailySummary(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("daily_summary")
    }
}