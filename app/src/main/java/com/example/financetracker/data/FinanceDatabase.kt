package com.example.financetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Finance database class with instance objects
@Database(entities = [User::class, Transaction::class] , version = 2, exportSchema = false)
abstract class FinanceDatabase : RoomDatabase() {

    abstract fun userDao(): UserDAO
    abstract fun transactionDao(): TransactionDAO

    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null

        fun getDatabase(context: Context): FinanceDatabase {
            return INSTANCE ?: synchronized(lock = this) {
                Room.databaseBuilder(
                    context,
                    FinanceDatabase::class.java,
                    "finance_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}