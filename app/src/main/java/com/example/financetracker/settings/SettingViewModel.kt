package com.example.financetracker.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.financetracker.data.SettingsRepository
import com.example.financetracker.workers.FinanceWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val darkMode = settingsRepository.darkMode.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val dailyNotification = settingsRepository.dailyNotification.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    fun toggleDarkMode() {
        viewModelScope.launch {
            settingsRepository.setDarkMode(!darkMode.value)
        }
    }

    fun toggleDailyNotification(context: Context, userId: Int) {
        viewModelScope.launch {
            val newValue = !dailyNotification.value
            settingsRepository.setDailyNotification(newValue)

            if (newValue) {
                enableDailySummary(context, userId)
            } else {
                disableDailySummary(context)
            }
        }
    }

    private fun enableDailySummary(context: Context, userId: Int) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val inputData = Data.Builder()
            .putInt("USER_ID", userId)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<FinanceWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "daily_notification",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
            )
    }

    private fun disableDailySummary(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("daily_notification")
    }
}