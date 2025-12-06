package com.example.financetracker.data

import kotlinx.coroutines.flow.Flow

class OfflineTransactionsRepository(private val transactionDao: TransactionDAO) : TransactionsRepository {

    override suspend fun insertTransaction(transaction: Transaction) = transactionDao.insert(transaction)

    override suspend fun updateTransaction(transaction: Transaction) = transactionDao.update(transaction)

    override suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)

    override fun getAllTransactionsStream(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    override fun getTransactionStream(id: Int): Flow<Transaction?> = transactionDao.getTransaction(id)

    override fun getTransactionsByUserId(userId: Int): Flow<List<Transaction>> = transactionDao.getTransactionsByUserId(userId)

    override fun getTransactionsByUserIdAndType(userId: Int, type: String): Flow<List<Transaction>> = transactionDao.getTransactionsByUserIdAndType(userId, type)

    override fun getTotalIncome(userId: Int): Flow<Double?> = transactionDao.getTotalIncome(userId)

    override fun getTotalExpenses(userId: Int): Flow<Double?> = transactionDao.getTotalExpenses(userId)

    override fun getBalance(userId: Int): Flow<Double?> = transactionDao.getBalance(userId)

    override suspend fun deleteAllTransactionsByUserId(userId: Int) = transactionDao.deleteAllTransactionsByUserId(userId)
}