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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import com.example.financetracker.data.Transaction
import com.example.financetracker.transaction.TransactionCard
import com.example.financetracker.ui.theme.FinanceTrackerTheme
import com.example.financetracker.user.UserViewModel
import androidx.compose.runtime.collectAsState
import com.example.financetracker.data.User

@Composable
fun DashboardScreen(modifier: Modifier = Modifier,
                    onNavigateToHome: () -> Unit = {},
                    onNavigateToTransactions: () -> Unit = {},
                    onNavigateToSettings: () -> Unit = {},
                    onNavigateToAddTransaction: () -> Unit = {},
                    onNavigateToEditTransaction: () -> Unit = {},
                    viewModel: UserViewModel ) {

    var selectedScreen by remember { mutableStateOf("dashboard") }

    // Fake Transaction Data for testing before database is added
    val transactions = remember {
        listOf(
            Transaction(
                1,
                12.00,
                "Withdrawal",
                "Subscription",
                "Netflix Subscription",
                System.currentTimeMillis()
            ),
            Transaction(2, 25.00, "Withdrawal", "Food", "Lunch at cafe", System.currentTimeMillis())
        )
    }

    // Values to be used and got from the db
    var balance by remember { mutableStateOf(5153.00) }
    var totalIncome by remember { mutableStateOf(1000.00) }
    var totalExpenses by remember { mutableStateOf(500.00) }

    Column(
        modifier = modifier
            .padding(24.dp)
            .background(Color.White)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
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

        Spacer(Modifier.height(50.dp))

        // Balance and income or expense card at top of screen
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Total Balance",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "€${viewModel.currentUser.collectAsState().value?.balance}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Income/Expenses",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    Text(
                        "+€$totalIncome",
                        fontSize = 24.sp,
                        color = Color.Green
                    )
                    Text(
                        "-€$totalExpenses",
                        fontSize = 24.sp,
                        color = Color.Red
                    )
                }
            }
        }

        // Add transaction button
        Spacer(Modifier.height(35.dp))

        Button(
            onClick = { onNavigateToAddTransaction() },
            modifier = Modifier
                .width(160.dp)
                .height(52.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = null)
            Spacer(Modifier.width(5.dp))
            Text("Add")
        }

        // Navigation bar from nav button function
        Spacer(Modifier.height(15.dp))

        NavButtons(
            onNavigateToHome = onNavigateToHome,
            onNavigateToTransactions = onNavigateToTransactions,
            onNavigateToSettings = onNavigateToSettings,
            selectedScreen = selectedScreen
        )

        // Transaction list
        Spacer(Modifier.height(25.dp))

        HorizontalDivider()

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
}


// Navigation button function
@Composable
fun NavButtons(
    onNavigateToHome: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    selectedScreen: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterChip(
            modifier = Modifier
                .weight(1f),
            selected = selectedScreen == "dashboard",
            onClick = onNavigateToHome,
            label = { Text("Dashboard", fontSize = 10.sp) },
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
        )

        FilterChip(
            modifier = Modifier
                .weight(1f),
            selected = selectedScreen == "transactions",
            onClick = onNavigateToTransactions,
            label = { Text("Transactions", fontSize = 10.sp) },
            leadingIcon = { Icon(Icons.Default.Menu, contentDescription = null) }
        )

        FilterChip(
            modifier = Modifier
                .weight(1f),
            selected = selectedScreen == "settings",
            onClick = onNavigateToSettings,
            label = { Text("Settings", fontSize = 10.sp) },
            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
        )
    }
}


@Preview(showBackground = true, showSystemUi = true )
@Composable
fun DashboardPreview() {
    FinanceTrackerTheme {
        /*DashboardScreen(
            onNavigateToHome = {},
            onNavigateToTransactions = {},
            onNavigateToSettings = {},
            onNavigateToAddTransaction = {}
        )*/
    }
}