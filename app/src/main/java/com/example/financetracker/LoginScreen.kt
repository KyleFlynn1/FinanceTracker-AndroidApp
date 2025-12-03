package com.example.financetracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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

@Composable
fun LoginScreen(modifier: Modifier = Modifier,
                onLogin : () -> Unit = {},
                onRegisterSwitch: () -> Unit = {}) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
        Spacer(Modifier.height(90.dp))

        Text(
            text = "Login",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.DarkGray,
            modifier = modifier
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text("Email")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { Text("Password")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(35.dp))

        Button(
            onClick = { onLogin() },
            modifier = Modifier
                .width(160.dp)
                .height(52.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Icon(Icons.Default.Star, contentDescription = null)
            Spacer(Modifier.width(5.dp))
            Text("Login")
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { onRegisterSwitch() },
            modifier = Modifier
                .width(250.dp)
                .height(52.dp),
            shape = RoundedCornerShape(30.dp)
        ) { Text("Need To Register?") }

    }
}
fun onLogin() {
}

fun onRegisterSwitch() {
}

@Preview(showBackground = true, showSystemUi = true )
@Composable
fun LoginPreview() {
    FinanceTrackerTheme {
        LoginScreen()
    }
}