package com.example.financetracker.data
import androidx.room.Entity
import androidx.room.PrimaryKey

// Transaction class
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userId: Int,
    val amount: Double,
    val type: String,
    val description: String,
    val notes: String,
    val date: Long
)