package com.example.financetracker

import android.app.Application
import com.example.financetracker.data.AppContainer
import com.example.financetracker.data.AppDataContainer

class FinanceTrackerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}