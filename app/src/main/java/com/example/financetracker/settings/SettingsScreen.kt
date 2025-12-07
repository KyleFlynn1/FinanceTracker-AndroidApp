package com.example.financetracker.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                   viewModel: UserViewModel,
                   settingViewModel: SettingViewModel) {

    val context = LocalContext.current
    val darkMode by settingViewModel.darkMode.collectAsState()
    val dailyNotification by settingViewModel.dailyNotification.collectAsState()

    var selectedScreen by remember { mutableStateOf("settings") }


    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.displayLarge
        )
        HorizontalDivider()

        Spacer(Modifier.height(35.dp))

        // Dark Mode Toggle
        SettingsSwitch(
            title = "Dark Mode",
            description = "Enable dark theme",
            checked = darkMode,
            onCheckedChange = { settingViewModel.toggleDarkMode() }
        )

        HorizontalDivider()

        // Daily Notifications Toggle
        SettingsSwitch(
            title = "Daily Notifications",
            description = "Receive daily financial summaries",
            checked = dailyNotification,
            onCheckedChange = { enabled ->
                viewModel.currentUser.value?.let { user ->
                    settingViewModel.toggleDailyNotification(context, user.id)

                    val testWork = OneTimeWorkRequestBuilder<FinanceWorker>()
                        .setInputData(workDataOf("USER_ID" to user.id))
                        .build()
                    WorkManager.getInstance(context).enqueue(testWork)
                }
            }
        )

        Button(
            onClick = {
                viewModel.logoutUser()
                onNavigateToLogin()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Icon(
                Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "Log Out",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
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

@Composable
fun SettingsSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
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