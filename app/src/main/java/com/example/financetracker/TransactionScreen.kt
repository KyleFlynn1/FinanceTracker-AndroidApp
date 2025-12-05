package com.example.financetracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.financetracker.data.Transaction

@Composable
fun TransactionScreen(modifier: Modifier = Modifier,
                      onNavigateToHome: () -> Unit = {},
                      onNavigateToTransactions: () -> Unit = {},
                      onNavigateToSettings: () -> Unit = {},
                      onNavigateToAddTransaction: () -> Unit = {},
                      onNavigateToEditTransaction: () -> Unit = {}) {

    var selectedScreen by remember { mutableStateOf("transactions") }

    // Fake Transaction Data for testing before database is added
    val transactions = remember {
        listOf(
            Transaction(1, 12.00, "Withdrawal", "Subscription", "Netflix Subscription", System.currentTimeMillis()),
            Transaction(2, 25.00, "Withdrawal", "Food", "Lunch at cafe", System.currentTimeMillis())
        )
    }
    Column(
        modifier = modifier
            .padding(24.dp)
            .background(Color.White)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Finance Tracker",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.DarkGray,
            modifier = modifier
                .align(Alignment.CenterHorizontally)
        )
        HorizontalDivider()

        // Transaction list
        Spacer(Modifier.height(25.dp))

        Text(
            "Recent Transaction",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        // Transaction cards in list to be got from the database
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transactions) { transaction ->
                TransactionCard(transaction, onNavigateToEditTransaction = onNavigateToEditTransaction)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom)
    {
        // Navigation bar from nav button function
        Spacer(Modifier.height(15.dp))

        NavButtons(
            onNavigateToHome = onNavigateToHome,
            onNavigateToTransactions = onNavigateToTransactions,
            onNavigateToSettings = onNavigateToSettings,
            selectedScreen = selectedScreen
        )

        // Add transaction button

        Button(
            onClick = { onNavigateToAddTransaction() },
            modifier = Modifier
                .width(240.dp)
                .height(52.dp),
            shape = RoundedCornerShape(30.dp),
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = null)
            Spacer(Modifier.width(5.dp))
            Text("Add Transaction")
        }
    }
}

@Composable
fun TransactionCard(
    transaction: Transaction,
                    onNavigateToEditTransaction: () -> Unit = {} ) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
                "€${transaction.amount}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Button(
                onClick = { onNavigateToEditTransaction() },
                modifier = Modifier
                    .width(90.dp)
                    .height(35.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
                Spacer(Modifier.width(5.dp))
                Text("Edit", fontSize = 10.sp)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true )
@Composable
fun TransactionPreview() {
    FinanceTrackerTheme {
        TransactionScreen()
    }
}