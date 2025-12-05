package com.example.financetracker.data

import android.content.Context

// App container for dependency injection
interface AppContainer {
    val usersRepository: UsersRepository
    val transactionsRepository: TransactionsRepository
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
}