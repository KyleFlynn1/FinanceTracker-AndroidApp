package com.example.financetracker.data

import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    // Basic CRUD operations
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)

    // Get transactions
    fun getAllTransactionsStream(): Flow<List<Transaction>>

    fun getTransactionStream(id: Int): Flow<Transaction?>

    // User operations
    fun getTransactionsByUserId(userId: Int): Flow<List<Transaction>>

    fun getTransactionsByUserIdAndType(userId: Int, type: String): Flow<List<Transaction>>

    // Financial calculations
    fun getTotalIncome(userId: Int): Flow<Double?>

    fun getTotalExpenses(userId: Int): Flow<Double?>

    fun getBalance(userId: Int): Flow<Double?>

    // User management
    suspend fun deleteAllTransactionsByUserId(userId: Int)
}