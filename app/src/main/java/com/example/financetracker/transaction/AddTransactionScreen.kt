package com.example.financetracker.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.ui.theme.FinanceTrackerTheme

@Composable
fun AddTransactionScreen(
    modifier: Modifier = Modifier,
    onSubmitTransaction: () -> Unit = {},
    viewModel: TransactionViewModel
) {
    var description by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Income") }

    val transactionUiState by viewModel.transactionUiState.collectAsState()

    LaunchedEffect(transactionUiState) {
        if (transactionUiState is TransactionUiState.Success) {
            // Clear fields after successful submission
            description = ""
            cost = ""
            notes = ""
            type = "Income"
            onSubmitTransaction()
            viewModel.resetTransactionState()
        }
    }

    AddTransactionScreenContent(
        description = description,
        cost = cost,
        notes = notes,
        type = type,
        onDescriptionChange = { description = it },
        onCostChange = { cost = it },
        onNotesChange = { notes = it },
        onTypeChange = { type = it },
        onSubmitClick = {
            val amount = cost.toDoubleOrNull() ?: Double.NaN
            viewModel.addTransaction(
                amount = amount,
                type = type,
                description = description,
                notes = notes
            )
        },
        errorMessage = if (transactionUiState is TransactionUiState.Error) {
            (transactionUiState as TransactionUiState.Error).message
        } else null,
        isLoading = transactionUiState is TransactionUiState.Loading,
        modifier = modifier
    )
}

@Composable
private fun AddTransactionScreenContent(
    description: String,
    cost: String,
    notes: String,
    type: String,
    onDescriptionChange: (String) -> Unit,
    onCostChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    // Transaction type button values
    val options = listOf("Income", "Expense")

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Finance Tracker",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        HorizontalDivider()
        Spacer(Modifier.height(90.dp))

        Text(
            text = "Add Transaction",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(20.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = cost,
            onValueChange = onCostChange,
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(20.dp))

        Column(modifier = Modifier.padding(16.dp)) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = type == label,
                        onClick = { onTypeChange(label) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        icon = {
                            if (type == label) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Text(label)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onSubmitClick,
            modifier = Modifier
                .width(250.dp)
                .height(52.dp),
            shape = RoundedCornerShape(30.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(Icons.Default.Star, contentDescription = null)
                Spacer(Modifier.width(5.dp))
                Text("Submit Transaction")
            }
        }
    }
}