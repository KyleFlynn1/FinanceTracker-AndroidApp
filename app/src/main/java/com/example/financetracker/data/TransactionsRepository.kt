package com.example.financetracker.data

import kotlinx.coroutines.flow.Flow


// Repo that provides insert update and delete and retrieve of [Transaction] from a given source

interface TransactionsRepository {
    // Retrieve all the Transactions from the given data source
    fun getAllTransactionsStream(): Flow<List<Transaction>>

    // Retrieve a Transaction from the given data source that matches with the [id]
    fun getTransactionStream(id: Int): Flow<Transaction?>

    // Insert Transaction in the data source
    suspend fun insertTransaction(transaction: Transaction)

    // Delete Transaction from the data source
    suspend fun deleteTransaction(transaction: Transaction)

    // Update Transaction in the data source
    suspend fun updateTransaction(transaction: Transaction)
}