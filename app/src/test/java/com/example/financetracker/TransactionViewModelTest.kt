package com.example.financetracker

import app.cash.turbine.test
import com.example.financetracker.data.Transaction
import com.example.financetracker.data.TransactionsRepository
import com.example.financetracker.data.UsersRepository
import com.example.financetracker.transaction.TransactionUiState
import com.example.financetracker.transaction.TransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


// TRANSACTION VIEW MODEL UNIT TESTS
@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    // A mock repository for testing
    @Mock
    private lateinit var mockRepository: TransactionsRepository

    @Mock
    private lateinit var mockUsersRepository: UsersRepository

    // The view model to be tested
    private lateinit var viewModel: TransactionViewModel

    // Test dispatcher and scope
    private val testDispatcher = StandardTestDispatcher()

    // Set up before each test
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = TransactionViewModel(mockRepository, mockUsersRepository)
    }

    // After each test
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /*
        STATE TESTS
     */

    // Test 1-  Initial state set to IDLE
    @Test
    fun `initial state should be Idle`() = runTest {
        viewModel.transactionUiState.test {
            val state = awaitItem()
            assertTrue(state is TransactionUiState.Idle)
        }
    }

    // Test 2 - Initial transactions list should be empty
    @Test
    fun `initial transactions list should be empty`() = runTest {
        viewModel.transactions.test {
            val list = awaitItem()
            assertTrue(list.isEmpty())
        }
    }

    /*
    ADDING TRANSACTOIN TESTS
     */

    // Test 3 - Add transaction with valid data should succeed
    @Test
    fun `addTransaction with valid data should succeed`() = runTest {
        // ARRANGE
        val userId = 1
        viewModel.setCurrentUser(userId)

        whenever(mockRepository.getTransactionsByUserId(userId))
            .thenReturn(flowOf(emptyList()))

        // ACT
        viewModel.addTransaction(
            amount = 50.0,
            type = "Expense",
            description = "Coffee",
            notes = "Morning coffee"
        )
        advanceUntilIdle()

        // ASSERT
        viewModel.transactionUiState.test {
            val state = awaitItem()
            assertTrue(state is TransactionUiState.Success)
            assertEquals("Transaction added successfully", (state as TransactionUiState.Success).message)
        }

        verify(mockRepository, times(1)).insertTransaction(any())
    }

    // Test 4 - Add transaction with invalid amount should fail
    @Test
    fun `addTransaction with invalid amount should fail`() = runTest {
        // ARRANGE
        viewModel.setCurrentUser(1)

        // ACT
        viewModel.addTransaction(
            amount = 0.0, // Invalid amount
            type = "Expense",
            description = "Test",
            notes = "Test"
        )

        // ASSERT
        viewModel.transactionUiState.test {
            val state = awaitItem()
            assertTrue(state is TransactionUiState.Error)
            assertEquals("Please enter a valid amount", (state as TransactionUiState.Error).message)
        }

        verify(mockRepository, never()).insertTransaction(any())
    }

    // Test 5 - Add transaction with empty type should fail
    @Test
    fun `addTransaction with empty description should fail`() = runTest {
        // ARRANGE
        viewModel.setCurrentUser(1)

        // ACT
        viewModel.addTransaction(
            amount = 50.0,
            type = "Expense",
            description = "",
            notes = "Test"
        )

        // ASSERT
        viewModel.transactionUiState.test {
            val state = awaitItem()
            assertTrue(state is TransactionUiState.Error)
            assertEquals("Please enter a description", (state as TransactionUiState.Error).message)
        }

        verify(mockRepository, never()).insertTransaction(any())
    }

    // Test 6 - Add transaction without logged in user should fail
    @Test
    fun `addTransaction without logged in user should fail`() = runTest {
        // ACT
        viewModel.addTransaction(
            amount = 50.0,
            type = "Expense",
            description = "Test",
            notes = "Test"
        )

        // ASSERT
        viewModel.transactionUiState.test {
            val state = awaitItem()
            assertTrue(state is TransactionUiState.Error)
            assertEquals("User not logged in", (state as TransactionUiState.Error).message)
        }

        verify(mockRepository, never()).insertTransaction(any())
    }

    /*
        DELETE TRANSACTION TESTS
     */

    // Test 7 - Delete transaction should succeed for authorized user
    @Test
    fun `deleteTransaction should succeed for authorized user`() = runTest {
        // ARRANGE
        val userId = 1
        viewModel.setCurrentUser(userId)

        val transaction = Transaction(
            id = 1,
            userId = userId,
            amount = 50.0,
            type = "Expense",
            description = "Coffee",
            notes = "Test",
            date = System.currentTimeMillis()
        )

        whenever(mockRepository.getTransactionsByUserId(userId))
            .thenReturn(flowOf(emptyList()))

        // ACT
        viewModel.deleteTransaction(transaction)
        advanceUntilIdle()

        // ASSERT
        viewModel.transactionUiState.test {
            val state = awaitItem()
            assertTrue(state is TransactionUiState.Success)
            assertEquals("Transaction deleted successfully", (state as TransactionUiState.Success).message)
        }

        verify(mockRepository, times(1)).deleteTransaction(transaction)
    }

    // Test 8 - Delete transaction should fail for unauthorized user

    @Test
    fun `deleteTransaction should fail for unauthorized user`() = runTest {
        // ARRANGE
        viewModel.setCurrentUser(1)

        val transaction = Transaction(
            id = 1,
            userId = 2,
            amount = 50.0,
            type = "Expense",
            description = "Coffee",
            notes = "Test",
            date = System.currentTimeMillis()
        )

        // ACT
        viewModel.deleteTransaction(transaction)
        advanceUntilIdle()

        // ASSERT
        viewModel.transactionUiState.test {
            val state = awaitItem()
            assertTrue(state is TransactionUiState.Error)
            assertEquals("Unauthorized action", (state as TransactionUiState.Error).message)
        }

        verify(mockRepository, never()).deleteTransaction(any())
    }

    /*
        BALANCE CALUCLATION TESTS
     */
    // Test 9 - Calculate total income should sum all income transactions
    @Test
    fun `calculateTotalIncome should sum all income transactions`() = runTest {
        // ARRANGE
        val userId = 1
        viewModel.setCurrentUser(userId)

        val transactions = listOf(
            Transaction(1, userId, 100.0, "Income", "Salary", "Test", System.currentTimeMillis()),
            Transaction(2, userId, 50.0, "Income", "Bonus", "Test", System.currentTimeMillis()),
            Transaction(3, userId, 30.0, "Expense", "Coffee", "Test", System.currentTimeMillis())
        )

        whenever(mockRepository.getTransactionsByUserId(userId))
            .thenReturn(flowOf(transactions))

        viewModel.loadTransactionsForUser(userId)
        advanceUntilIdle()

        // ACT
        val totalIncome = viewModel.calculateTotalIncome()

        // ASSERT
        assertEquals(150.0, totalIncome, 0.01) // 100 + 50
    }

    // Test 10 - Calculate total expenses should sum all expense transactions
    @Test
    fun `calculateTotalExpenses should sum all expense transactions`() = runTest {
        // ARRANGE
        val userId = 1
        viewModel.setCurrentUser(userId)

        val transactions = listOf(
            Transaction(1, userId, 100.0, "Income", "Salary", "Test", System.currentTimeMillis()),
            Transaction(2, userId, 30.0, "Expense", "Coffee", "Test", System.currentTimeMillis()),
            Transaction(3, userId, 20.0, "Expense", "Lunch", "Test", System.currentTimeMillis())
        )

        whenever(mockRepository.getTransactionsByUserId(userId))
            .thenReturn(flowOf(transactions))

        viewModel.loadTransactionsForUser(userId)
        advanceUntilIdle()

        // ACT
        val totalExpenses = viewModel.calculateTotalExpenses()

        // ASSERT
        assertEquals(50.0, totalExpenses, 0.01) // 30 + 20
    }

    // Test 11 - Calculate balance should return income minus expenses
    @Test
    fun `calculateBalance should return income minus expenses`() = runTest {
        // ARRANGE
        val userId = 1
        viewModel.setCurrentUser(userId)

        val transactions = listOf(
            Transaction(1, userId, 100.0, "Income", "Salary", "Test", System.currentTimeMillis()),
            Transaction(2, userId, 30.0, "Expense", "Coffee", "Test", System.currentTimeMillis())
        )

        whenever(mockRepository.getTransactionsByUserId(userId))
            .thenReturn(flowOf(transactions))

        viewModel.loadTransactionsForUser(userId)
        advanceUntilIdle()

        // ACT
        val balance = viewModel.calculateBalance()

        // ASSERT
        assertEquals(70.0, balance, 0.01)
    }

}
