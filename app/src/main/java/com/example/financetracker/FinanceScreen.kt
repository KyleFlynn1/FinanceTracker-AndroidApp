package com.example.financetracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
                    onLoginSwitch = { navController.navigate("login") }
                )
            }
            composable("login") {
                LoginScreen(
                    onLogin = { navController.navigate("home") },
                    onRegisterSwitch = { navController.navigate("signup") }
                )
            }
            composable("home") {
                DashboardScreen()
            }
            composable("transactions") {
                TransactionScreen()
            }
            composable(route = "addTransaction") {
                AddTransactionScreen()
            }
            composable(route = "editTransaction") {
                EditTransactionScreen()
            }
        }
    }
}
