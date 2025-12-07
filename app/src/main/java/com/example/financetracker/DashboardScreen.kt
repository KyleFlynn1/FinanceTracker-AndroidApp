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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.transaction.TransactionCard
import com.example.financetracker.ui.theme.FinanceTrackerTheme
import com.example.financetracker.user.UserViewModel
import androidx.compose.runtime.collectAsState
import com.example.financetracker.transaction.TransactionViewModel

@Composable
fun DashboardScreen(modifier: Modifier = Modifier,
                    onNavigateToHome: () -> Unit = {},
                    onNavigateToTransactions: () -> Unit = {},
                    onNavigateToSettings: () -> Unit = {},
                    onNavigateToAddTransaction: () -> Unit = {},
                    onNavigateToEditTransaction: (Int) -> Unit = {},
                    viewModel: UserViewModel,
                    transactionViewModel: TransactionViewModel) {

    var selectedScreen by remember { mutableStateOf("dashboard") }

    // Get transacctions for user from the viewmodel
    val transactions by transactionViewModel.transactions.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Values to be used and got from the db
    val balance = transactionViewModel.calculateBalance()
    val totalIncome = transactionViewModel.calculateTotalIncome()
    val totalExpenses = transactionViewModel.calculateTotalExpenses()

    Column(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.displayLarge
        )
        HorizontalDivider()

        Spacer(Modifier.height(50.dp))

        // Balance and income or expense card at top of screen
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    "€%.2f".format(balance),
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
                        "+€%.2f".format(totalIncome),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "-€%.2f".format(totalExpenses),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
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


// Navigation button function to be used in the dashboard screen and reused in other screens
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
                .weight(1f)
                .height(48.dp),
            selected = selectedScreen == "dashboard",
            onClick = onNavigateToHome,
            label = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(20.dp)) }
        )

        FilterChip(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            selected = selectedScreen == "transactions",
            onClick = onNavigateToTransactions,
            label = { Text("Transactions", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            leadingIcon = { Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(20.dp)) }
        )

        FilterChip(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            selected = selectedScreen == "settings",
            onClick = onNavigateToSettings,
            label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(20.dp)) }
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