package com.example.financetracker.transaction

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financetracker.camera.CameraCapture
import java.io.File

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

    var photoPath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newPath = result.data?.getStringExtra("photo_path")
            if (!newPath.isNullOrEmpty()) photoPath = newPath
        }
    }

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
                notes = notes,
                photoPath = photoPath
            )
        },
        errorMessage = if (transactionUiState is TransactionUiState.Error) {
            (transactionUiState as TransactionUiState.Error).message
        } else null,
        isLoading = transactionUiState is TransactionUiState.Loading,
        modifier = modifier,
        photoPath = photoPath,
        onPhotoPathChange = { photoPath = it },
        context = context,
        cameraLauncher = cameraLauncher
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
    modifier: Modifier = Modifier,
    photoPath: String?,
    onPhotoPathChange: (String?) -> Unit,
    context: android.content.Context,
    cameraLauncher: ActivityResultLauncher<Intent>
) {
    // Transaction type button values
    val options = listOf("Income", "Expense")

    Column(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Finance Tracker",
            style = MaterialTheme.typography.displayMedium
        )
        HorizontalDivider()
        Spacer(Modifier.height(45.dp))

        Text(
            text = "Add Transaction",
            style = MaterialTheme.typography.displayMedium
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
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = cost,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    onCostChange(newValue)
                }
            },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            prefix = { Text("€") },
            singleLine = true
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

        Text(
            text = "Optional Receipt Photo",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))
        // Photo button using cameraa captuer

        if (photoPath != null && File(photoPath!!).exists()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val bitmap = remember(photoPath) { BitmapFactory.decodeFile(photoPath) }
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Receipt",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(Modifier.height(12.dp))  // More space
                Button(
                    onClick = { onPhotoPathChange(null) },
                    modifier = Modifier.fillMaxWidth(0.6f)  // Make it wider
                ) {
                    Text("Remove Photo")
                }
            }
        } else {
            Button(
                onClick = {
                    val intent = Intent(context, CameraCapture::class.java)
                    cameraLauncher.launch(intent)
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Add Photo (optional)")
            }
        }


        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onSubmitClick,
            modifier = Modifier
                .width(250.dp)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
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