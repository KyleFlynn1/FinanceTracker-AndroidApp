package com.example.financetracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// App container for dependency injection
interface AppContainer {
    val usersRepository: UsersRepository
    val transactionsRepository: TransactionsRepository

    val settingsRepository: SettingsRepository
}

// [AppContainer] implementation that provides instance of [OfflineUsersRepository]
class AppDataContainer(private val context: Context) : AppContainer {
    // Implementation for [UsersRepository]
    override val usersRepository: UsersRepository by lazy {
        OfflineUsersRepository(FinanceDatabase.getDatabase(context).userDao())
    }
    // Implementation for [TransactionsRepository]
    override val transactionsRepository: TransactionsRepository by lazy {
        OfflineTransactionsRepository(FinanceDatabase.getDatabase(context).transactionDao())
    }

    // Add Settings datastore repo
    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(context.dataStore)
    }
}