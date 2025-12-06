package com.example.financetracker.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.financetracker.NavButtons
import com.example.financetracker.ui.theme.FinanceTrackerTheme
import com.example.financetracker.user.UserViewModel
import com.example.financetracker.workers.FinanceWorker

@Composable
fun SettingsScreen(modifier: Modifier = Modifier,
                   onNavigateToHome: () -> Unit = {},
                   onNavigateToTransactions: () -> Unit = {},
                   onNavigateToSettings: () -> Unit = {},
                   onNavigateToLogin: () -> Unit = {},
                   viewModel: UserViewModel) {

    val settingViewModel: SettingViewModel = viewModel()
    val context = LocalContext.current
    var selectedScreen by remember { mutableStateOf("settings") }

    var summaryEnabled by remember { mutableStateOf(true) }


    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.DarkGray,
            modifier = modifier
                .align(Alignment.CenterHorizontally)
        )
        HorizontalDivider()

        Spacer(Modifier.height(35.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Daily Spend Summary")
            Switch(
                checked = summaryEnabled,
                onCheckedChange = { enabled ->
                    summaryEnabled = enabled
                    val userId = viewModel.currentUser.value!!.id

                    if (enabled) {
                        settingViewModel.enableDailySummary(context, userId)
                    } else {
                        settingViewModel.disableDailySummary(context)
                    }

                    // Comment out beloww to not have a noti everytime you use switch its very demonstration
                    val testWork = OneTimeWorkRequestBuilder<FinanceWorker>()
                        .setInputData(workDataOf("USER_ID" to userId))
                        .build()
                    WorkManager.getInstance(context).enqueue(testWork)
                },
            )
        }

        Spacer(Modifier.height(35.dp))

        Button(
            onClick = {
                viewModel.logoutUser()
                onNavigateToLogin()
            },
            modifier = Modifier
                .width(160.dp)
                .height(35.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(5.dp))
            Text("Log Out", fontSize = 10.sp)
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
    }
}

@Preview(showBackground = true, showSystemUi = true )
@Composable
fun SettingsScreenPreview() {
    FinanceTrackerTheme {
        //SettingsScreen()
    }
}