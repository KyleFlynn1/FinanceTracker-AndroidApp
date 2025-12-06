package com.example.financetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransaction(id: Int): Flow<Transaction?>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getTransactionsByUserId(userId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY date DESC")
    fun getTransactionsByUserIdAndType(userId: Int, type: String): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'Income'")
    fun getTotalIncome(userId: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'Expense'")
    fun getTotalExpenses(userId: Int): Flow<Double?>

    @Query("SELECT SUM(CASE WHEN type = 'Income' THEN amount ELSE -amount END) FROM transactions WHERE userId = :userId")
    fun getBalance(userId: Int): Flow<Double?>

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAllTransactionsByUserId(userId: Int)
}