package com.example.financetracker.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionsRepository
import com.example.financetracker.data.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val transactionRepository: TransactionsRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _transactionUiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Idle)
    val transactionUiState: StateFlow<TransactionUiState> = _transactionUiState.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    val totalIncome: StateFlow<Double> = _transactions
        .map { list -> list.filter { it.type.equals("Income", ignoreCase = true) }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val totalExpenses: StateFlow<Double> = _transactions
        .map { list -> list.filter { it.type.equals("Expense", ignoreCase = true) }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val balance: StateFlow<Double> = totalIncome
        .combine(totalExpenses) { inc, exp -> inc - exp }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
    // Store the current logged in user ID
    private var currentUserId: Int? = null

    // Set the current user (call this when user logs in)
    fun setCurrentUser(userId: Int) {
        currentUserId = userId
        loadTransactionsForUser(userId)
    }

    // Load transactions for specific user
    fun loadTransactionsForUser(userId: Int) {
        viewModelScope.launch {
            try {
                transactionRepository.getTransactionsByUserId(userId).collect { transactionList ->
                    _transactions.value = transactionList
                }
            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to load transactions: ${e.message}")
            }
        }
    }

    // Add transaction for current user
    fun addTransaction(amount: Double, type: String, description: String, notes: String) {
        val userId = currentUserId
        if (userId == null) {
            _transactionUiState.value = TransactionUiState.Error("User not logged in")
            return
        }

        if (!validateTransaction(amount, type, description, notes)) {
            return
        }

        viewModelScope.launch {
            try {
                _transactionUiState.value = TransactionUiState.Loading

                val newTransaction = Transaction(
                    id = 0,
                    userId = userId,  // Link to current user
                    amount = amount,
                    type = type,
                    description = description,
                    notes = notes,
                    date = System.currentTimeMillis()
                )

                transactionRepository.insertTransaction(newTransaction)
                updateUserBalance()
                _transactionUiState.value = TransactionUiState.Success("Transaction added successfully")

            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to add transaction: ${e.message}")
            }
        }
    }

    // Update transaction
    fun updateTransaction(id: Int, amount: Double, type: String, description: String, notes: String) {
        val userId = currentUserId
        if (userId == null) {
            _transactionUiState.value = TransactionUiState.Error("User not logged in")
            return
        }

        if (!validateTransaction(amount, type, description, notes)) {
            return
        }

        viewModelScope.launch {
            try {
                _transactionUiState.value = TransactionUiState.Loading

                val updatedTransaction = Transaction(
                    id = id,
                    userId = userId,
                    amount = amount,
                    type = type,
                    description = description,
                    notes = notes,
                    date = System.currentTimeMillis()
                )

                transactionRepository.updateTransaction(updatedTransaction)
                updateUserBalance()
                _transactionUiState.value = TransactionUiState.Success("Transaction updated successfully")

            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to update transaction: ${e.message}")
            }
        }
    }

    // Delete transaction
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                // Verify transaction belongs to current user
                if (transaction.userId != currentUserId) {
                    _transactionUiState.value = TransactionUiState.Error("Unauthorized action")
                    return@launch
                }

                _transactionUiState.value = TransactionUiState.Loading
                transactionRepository.deleteTransaction(transaction)
                updateUserBalance()
                _transactionUiState.value = TransactionUiState.Success("Transaction deleted successfully")
            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to delete transaction: ${e.message}")
            }
        }
    }

    // Get transactions by type for current user
    fun getTransactionsByType(type: String) {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            try {
                transactionRepository.getTransactionsByUserIdAndType(userId, type).collect { transactionList ->
                    _transactions.value = transactionList
                }
            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to filter transactions: ${e.message}")
            }
        }
    }

    // Get transaction by ID (for editing)
    fun getTransactionById(id: Int) {
        viewModelScope.launch {
            try {
                transactionRepository.getTransactionStream(id).collect { transaction ->
                    _selectedTransaction.value = transaction
                }
            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to load transaction: ${e.message}")
            }
        }
    }

    // Calculate balance which is Income - Expenses
    fun calculateBalance(): Double {
        return calculateTotalIncome() - calculateTotalExpenses()
    }

    private fun updateUserBalance() {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            try {
                val currentTransactions = transactionRepository.getTransactionsByUserId(userId).first()
                val income = currentTransactions
                    .filter { it.type.equals("Income", ignoreCase = true) }
                    .sumOf { it.amount }
                val expenses = currentTransactions
                    .filter { it.type.equals("Expense", ignoreCase = true) }
                    .sumOf { it.amount }
                val newBalance = income - expenses
                usersRepository.updateUserBalance(userId, newBalance)
            } catch (e: Exception) {
            }
        }
    }

    // Calculate total income
    fun calculateTotalIncome(): Double {
        return _transactions.value
            .filter { it.type.equals("Income", ignoreCase = true) }
            .sumOf { it.amount }
    }

    // Calculate total expenses
    fun calculateTotalExpenses(): Double {
        return _transactions.value
            .filter { it.type.equals("Expense", ignoreCase = true) }
            .sumOf { it.amount }
    }


    // Validation helper
    private fun validateTransaction(amount: Double, type: String, description: String, notes: String): Boolean {
        if (amount.isNaN() || amount <= 0) {
            _transactionUiState.value = TransactionUiState.Error("Please enter a valid amount")
            return false
        }

        if (type.isBlank()) {
            _transactionUiState.value = TransactionUiState.Error("Please select a transaction type")
            return false
        }

        if (description.isBlank()) {
            _transactionUiState.value = TransactionUiState.Error("Please enter a description")
            return false
        }

        return true
    }

    // Reset state
    fun resetTransactionState() {
        _transactionUiState.value = TransactionUiState.Idle
    }

    // Clear user data on logout
    fun clearUserData() {
        currentUserId = null
        _transactions.value = emptyList()
        _selectedTransaction.value = null
        resetTransactionState()
    }
}

sealed class TransactionUiState {
    object Idle : TransactionUiState()
    object Loading : TransactionUiState()
    data class Success(val message: String) : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
}