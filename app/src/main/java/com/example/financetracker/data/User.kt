package com.example.financetracker.data
import androidx.room.Entity
import androidx.room.PrimaryKey

// User class
@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val email: String,
    val password: String,
    val balance: Double
)