package com.example.financetracker

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Data class to hold the UI state for the finance tracker.
 */
data class FinanceUiState(
    val placeholder: String = ""
)

/*
*   FinanceViewModel holds info about the users finance details and does calcualtions for statistics to be displayed
*/
class FinanceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()
}
