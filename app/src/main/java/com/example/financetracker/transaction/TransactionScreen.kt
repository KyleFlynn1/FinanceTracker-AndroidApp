package com.example.financetracker.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.NavButtons
import com.example.financetracker.ui.theme.FinanceTrackerTheme
import com.example.financetracker.data.Transaction

@Composable
fun TransactionScreen(modifier: Modifier = Modifier,
                      onNavigateToHome: () -> Unit = {},
                      onNavigateToTransactions: () -> Unit = {},
                      onNavigateToSettings: () -> Unit = {},
                      onNavigateToAddTransaction: () -> Unit = {},
                      onNavigateToEditTransaction: (Int) -> Unit = {},
                      viewModel: TransactionViewModel

) {

    var selectedScreen by remember { mutableStateOf("transactions") }
    // Get transactions from ViewModel
    val transactions by viewModel.transactions.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()) {
        // Fixed header at top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Transactions",
                style = MaterialTheme.typography.displayLarge
            )
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
            Text(
                "Recent Transaction",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        // Scrollable list takes remaining space
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            items(transactions) { transaction ->
                TransactionCard(transaction, onNavigateToEditTransaction = onNavigateToEditTransaction)
            }
        }

        // Fixed bottom navigation + button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavButtons(
                onNavigateToHome = onNavigateToHome,
                onNavigateToTransactions = onNavigateToTransactions,
                onNavigateToSettings = onNavigateToSettings,
                selectedScreen = selectedScreen
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onNavigateToAddTransaction() },
                modifier = Modifier
                    .width(240.dp)
                    .height(56.dp), // Increase height
                shape = RoundedCornerShape(12.dp), // Change from 30.dp
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Add Transaction",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction,
                    onNavigateToEditTransaction: (Int) -> Unit = {} ) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )

            Column(modifier = Modifier
                .weight(1f)) {
                Text(
                    transaction.description,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    transaction.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                "€%.2f".format(transaction.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Button(
                onClick = { onNavigateToEditTransaction(transaction.id) },
                modifier = Modifier
                    .width(80.dp)
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Text(
                    "Edit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true )
@Composable
fun TransactionPreview() {
    FinanceTrackerTheme {
        //TransactionScreen()
    }
}