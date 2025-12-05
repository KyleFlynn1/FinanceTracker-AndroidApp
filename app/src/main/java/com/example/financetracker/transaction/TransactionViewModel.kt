package com.example.financetracker.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(private val transactionRepository: TransactionsRepository) : ViewModel() {

    // UI state for transaction operations
    private val _transactionUiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Idle)
    val transactionUiState: StateFlow<TransactionUiState> = _transactionUiState.asStateFlow()

    // List of all transactions
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    // Currently selected transaction (for viewing/editing)
    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    init {
        loadTransactions()
    }

    // Load all transactions
    fun loadTransactions() {
        viewModelScope.launch {
            try {
                transactionRepository.getAllTransactionsStream().collect { transactionList ->
                    _transactions.value = transactionList
                }
            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to load transactions: ${e.message}")
            }
        }
    }

    // Add a new transaction
    fun addTransaction(amount: Double, type: String, description: String, notes: String) {
        if (!validateTransaction(amount, type, description, notes)) {
            return
        }

        viewModelScope.launch {
            try {
                _transactionUiState.value = TransactionUiState.Loading

                val newTransaction = Transaction(
                    id = 0,
                    amount = amount,
                    type = type,
                    description = description,
                    notes = notes,
                    date = System.currentTimeMillis()
                )

                transactionRepository.insertTransaction(newTransaction)
                _transactionUiState.value = TransactionUiState.Success("Transaction added successfully")

            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to add transaction: ${e.message}")
            }
        }
    }

    // Update an existing transaction
    fun updateTransaction(id: Long, amount: Double, type: String, description: String, notes: String) {
        if (!validateTransaction(amount, type, description, notes)) {
            return
        }

        viewModelScope.launch {
            try {
                _transactionUiState.value = TransactionUiState.Loading

                val updatedTransaction = Transaction(
                    id = 0,
                    amount = amount,
                    type = type,
                    description = description,
                    notes = notes,
                    date = System.currentTimeMillis()
                )

                transactionRepository.updateTransaction(updatedTransaction)
                _transactionUiState.value = TransactionUiState.Success("Transaction updated successfully")

            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to update transaction: ${e.message}")
            }
        }
    }

    // Delete a transaction
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                _transactionUiState.value = TransactionUiState.Loading
                transactionRepository.deleteTransaction(transaction)
                _transactionUiState.value = TransactionUiState.Success("Transaction deleted successfully")
            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to delete transaction: ${e.message}")
            }
        }
    }

    // Get transaction by ID
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

    // Select a transaction for editing/viewing
    fun selectTransaction(transaction: Transaction?) {
        _selectedTransaction.value = transaction
    }

    // Get transactions by type (Income/Expense)
    fun getTransactionsByType(type: String) {
        viewModelScope.launch {
            try {
                transactionRepository.getTransactionByTypeStream(type).collect { transactionList ->
                    _transactions.value = transactionList
                }
            } catch (e: Exception) {
                _transactionUiState.value = TransactionUiState.Error("Failed to filter transactions: ${e.message}")
            }
        }
    }

    // Calculate total balance
    fun calculateBalance(): Double {
        return _transactions.value.sumOf {
            if (it.type.equals("Income", ignoreCase = true)) it.amount else -it.amount
        }
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

        // Notes can be optional - remove this check if you want
        if (notes.isBlank()) {
            _transactionUiState.value = TransactionUiState.Error("Please enter notes")
            return false
        }

        return true
    }

    // Reset the UI state
    fun resetTransactionState() {
        _transactionUiState.value = TransactionUiState.Idle
    }
}

// UI state for transaction operations
sealed class TransactionUiState {
    object Idle : TransactionUiState()
    object Loading : TransactionUiState()
    data class Success(val message: String) : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
}