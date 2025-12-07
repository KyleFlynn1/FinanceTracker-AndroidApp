package com.example.financetracker

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionsRepository
import com.example.financetracker.data.User
import com.example.financetracker.data.UsersRepository
import com.example.financetracker.transaction.TransactionViewModel
import com.example.financetracker.user.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever


// Test UI for Dashboard screen
@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var mockUsersRepository: UsersRepository

    @Mock
    private lateinit var mockTransactionsRepository: TransactionsRepository

    private lateinit var userViewModel: UserViewModel
    private lateinit var transactionViewModel: TransactionViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        userViewModel = UserViewModel(mockUsersRepository)
        transactionViewModel = TransactionViewModel(mockTransactionsRepository, mockUsersRepository)
    }

    /*
        DISPLAY TEST
     */
    // Test 1 - Dashboard screen should display the correct title
    @Test
    fun dashboardScreen_displaysTitle() {
        // ARRANGE & ACT
        composeTestRule.setContent {
            DashboardScreen(
                viewModel = userViewModel,
                transactionViewModel = transactionViewModel
            )
        }

        // ASSERT
        composeTestRule
            .onAllNodesWithText("Dashboard")[0]
            .assertIsDisplayed()
    }

    // Test 2 - Dashboard screen should display the correct total balance
    @Test
    fun dashboardScreen_displaysTotalBalance() {
        // ARRANGE & ACT
        composeTestRule.setContent {
            DashboardScreen(
                viewModel = userViewModel,
                transactionViewModel = transactionViewModel
            )
        }

        // ASSERT
        composeTestRule
            .onNodeWithText("Total Balance")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Income/Expenses")
            .assertIsDisplayed()
    }

    // Test 3 - Dashboard screen should display the correct add button

    @Test
    fun dashboardScreen_displaysAddButton() {
        // ARRANGE & ACT
        composeTestRule.setContent {
            DashboardScreen(
                viewModel = userViewModel,
                transactionViewModel = transactionViewModel
            )
        }

        // ASSERT
        composeTestRule
            .onNodeWithText("Add")
            .assertIsDisplayed()
    }

    // Test 4 - Dashboard screen should display the correct navigation buttons
    @Test
    fun dashboardScreen_displaysNavigationButtons() {
        // ARRANGE & ACT
        composeTestRule.setContent {
            DashboardScreen(
                viewModel = userViewModel,
                transactionViewModel = transactionViewModel
            )
        }

        // ASSERT
        composeTestRule
            .onAllNodesWithText("Dashboard")[0]
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Transactions")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Settings")
            .assertIsDisplayed()
    }

    // Test 5 - Dashboard screen should display the correct recent transactions header
    @Test
    fun dashboardScreen_displaysRecentTransactionsHeader() {
        // ARRANGE & ACT
        composeTestRule.setContent {
            DashboardScreen(
                viewModel = userViewModel,
                transactionViewModel = transactionViewModel
            )
        }

        // ASSERT
        composeTestRule
            .onNodeWithText("Recent Transaction")
            .assertIsDisplayed()
    }

    /*
        INTERATCTION WITH UI TEST
     */
    //Test 6 - Add button should trigger navigation when clicked

    @Test
    fun addButton_triggersNavigationWhenClicked() {
        // ARRANGE
        var addClicked = false

        composeTestRule.setContent {
            DashboardScreen(
                viewModel = userViewModel,
                transactionViewModel = transactionViewModel,
                onNavigateToAddTransaction = { addClicked = true }
            )
        }

        // ACT
        composeTestRule
            .onNodeWithText("Add")
            .performClick()

        // ASSERT
        assert(addClicked)
    }

    //Test 7 - Dashboard button should trigger navigation when clicked
    @Test
    fun transactionsButton_triggersNavigationWhenClicked() {
        // ARRANGE
        var transactionsClicked = false

        composeTestRule.setContent {
            DashboardScreen(
                viewModel = userViewModel,
                transactionViewModel = transactionViewModel,
                onNavigateToTransactions = { transactionsClicked = true }
            )
        }

        // ACT
        composeTestRule
            .onNodeWithText("Transactions")
            .performClick()

        // ASSERT
        assert(transactionsClicked)
    }

    // Test 8 - Settings button should trigger navigation when clicked
    @Test
    fun settingsButton_triggersNavigationWhenClicked() {
        // ARRANGE
        var settingsClicked = false

        composeTestRule.setContent {
            DashboardScreen(
                viewModel = userViewModel,
                transactionViewModel = transactionViewModel,
                onNavigateToSettings = { settingsClicked = true }
            )
        }

        // ACT
        composeTestRule
            .onNodeWithText("Settings")
            .performClick()

        // ASSERT
        assert(settingsClicked)
    }

}