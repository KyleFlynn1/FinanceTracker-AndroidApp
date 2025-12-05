package com.example.financetracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.transaction.AddTransactionScreen
import com.example.financetracker.transaction.EditTransactionScreen
import com.example.financetracker.transaction.TransactionScreen
import com.example.financetracker.user.LoginScreen
import com.example.financetracker.user.SignupScreen
import com.example.financetracker.user.UserViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financetracker.data.AppContainer
import com.example.financetracker.data.AppDataContainer
import com.example.financetracker.ui.AppViewModelProvider
import com.example.financetracker.user.UserEntryViewModel

// Top app bar composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    TopAppBar(
        title = { Text("Finance Tracker") },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            }
        }
    )
}

// Actual NavHost composable to add screens and navigation
@Composable
fun FinanceApp(
    navController: NavHostController = rememberNavController()
) {
    // Get View Models
    val userViewModel: UserViewModel = viewModel(factory = AppViewModelProvider.Factory)

    Scaffold(
        topBar = {
            FinanceAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("signup") {
                // Signup screen on buttons clicked switch screens
                SignupScreen(
                    onRegister = { navController.navigate("home") },
                    onLoginSwitch = { navController.navigate("login") },
                    viewModel = userViewModel
                )
            }
            composable("login") {
                LoginScreen(
                    onLogin = { navController.navigate("home") },
                    onRegisterSwitch = { navController.navigate("signup") },
                    viewModel = userViewModel
                )
            }
            composable("home") {
                DashboardScreen(
                    onNavigateToHome = { navController.navigate("home") },
                    onNavigateToTransactions = { navController.navigate("transactions") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToAddTransaction = { navController.navigate("addTransaction") },
                    onNavigateToEditTransaction = { navController.navigate("editTransaction") }
                )
            }
            composable("transactions") {
                TransactionScreen(
                    onNavigateToHome = { navController.navigate("home") },
                    onNavigateToTransactions = { navController.navigate("transactions") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToAddTransaction = { navController.navigate("addTransaction") },
                    onNavigateToEditTransaction = { navController.navigate("editTransaction") }
                )
            }
            composable(route = "addTransaction") {
                AddTransactionScreen(
                    onSubmitTransaction = { navController.navigate("home") }
                )
            }
            composable(route = "editTransaction") {
                EditTransactionScreen(
                    onConfirmEdit = { navController.navigate("home") },
                    onDeleteTransaction = { navController.navigate("home") }
                )
            }
            composable(route = "settings") {
                SettingsScreen(
                    onNavigateToHome = { navController.navigate("home") },
                    onNavigateToTransactions = { navController.navigate("transactions") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
        }
    }
}
