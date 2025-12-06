package com.example.financetracker.user

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.TransactionsRepository
import com.example.financetracker.data.UserDAO
import com.example.financetracker.data.UsersRepository
import com.example.financetracker.transaction.TransactionViewModel
import com.example.financetracker.ui.theme.FinanceTrackerTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLogin: () -> Unit = {},
    onRegisterSwitch: () -> Unit = {},
    viewModel: UserViewModel,
    transactionViewModel: TransactionViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authUiState by viewModel.authUiState.collectAsState()

    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.LoginSuccess) {
            val user = (authUiState as AuthUiState.LoginSuccess).user
            transactionViewModel.setCurrentUser(user.id)
            onLogin()
            viewModel.resetAuthState()
        }
    }

    LoginScreenContent(
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onLoginClick = {
            viewModel.loginUser(email, password)
        },
        onRegisterClick = onRegisterSwitch,
        errorMessage = if (authUiState is AuthUiState.Error) {
            (authUiState as AuthUiState.Error).message
        } else null,
        isLoading = authUiState is AuthUiState.Loading,
        modifier = modifier
    )
}

@Composable
private fun LoginScreenContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Finance Tracker",
            style = MaterialTheme.typography.displayMedium
        )
        HorizontalDivider()
        Spacer(Modifier.height(90.dp))

        Text(
            text = "Login",
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
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(35.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .width(160.dp)
                .height(56.dp),
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
                Text("Login")
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .width(250.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            Text("Need To Register?")
        }
    }
}