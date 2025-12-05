package com.example.financetracker.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.financetracker.FinanceTrackerApplication
import com.example.financetracker.user.UserViewModel
import com.example.financetracker.user.UserEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // User View Models
        initializer {
            UserViewModel(financeTrackerApplication().container.usersRepository)
        }
        initializer {
            UserEntryViewModel(financeTrackerApplication().container.usersRepository)
        }

        // Transaction View models
    }
}

fun CreationExtras.financeTrackerApplication(): FinanceTrackerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FinanceTrackerApplication)