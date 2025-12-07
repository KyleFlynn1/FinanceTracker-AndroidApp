package com.example.financetracker

import app.cash.turbine.test
import com.example.financetracker.data.User
import com.example.financetracker.data.UsersRepository
import com.example.financetracker.user.AuthUiState
import com.example.financetracker.user.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


// USER VIEW MODEL UNIT TESTS
@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    // A mock repository for testing
    @Mock
    private lateinit var mockRepository: UsersRepository

    // The view model to be tested
    private lateinit var viewModel: UserViewModel

    // Test dispatcher and scope
    private val testDispatcher = StandardTestDispatcher()

    // Set up before each test
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = UserViewModel(mockRepository)
    }

    // After each test
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /*
        UI STATE TESTS
     */

    // Test 1 - Initial state should be set to Idle
    @Test
    fun `initial authUiState should be Idle`() = runTest {
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Idle)
        }
    }

    // Test 2 - No user should be logged in at the beginning
    @Test
    fun `initial currentUser should be null`() = runTest {
        viewModel.currentUser.test {
            val user = awaitItem()
            assertNull(user)
        }
    }

    /*
        REGISTRATION TESTS
     */

    // Test 3 - Registration with valid data should succeed
    @Test
    fun `registerUser with valid data should succeed`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password123"

        // ACT
        viewModel.registerUser(email, password, confirmPassword)
        advanceUntilIdle()

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.RegistrationSuccess)
            assertEquals(email, (state as AuthUiState.RegistrationSuccess).user.email)
        }

        // Verify repository was called to insert user
        verify(mockRepository, times(1)).insertUser(any())
    }

    // Test 4 - Registration with empty email should fail
    @Test
    fun `registerUser with empty email should fail`() = runTest {
        // ARRANGE
        val email = ""
        val password = "password123"
        val confirmPassword = "password123"

        // ACT
        viewModel.registerUser(email, password, confirmPassword)

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Error)
            assertEquals("Email cannot be empty", (state as AuthUiState.Error).message)
        }

        verify(mockRepository, never()).insertUser(any())
    }

    // Test 5 - Registration with empty password should fail
    @Test
    fun `registerUser with empty password should fail`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = ""
        val confirmPassword = ""

        // ACT
        viewModel.registerUser(email, password, confirmPassword)

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Error)
            assertEquals("Password cannot be empty", (state as AuthUiState.Error).message)
        }

        verify(mockRepository, never()).insertUser(any())
    }

    // Test 6 - Registration with mismatched passwords should fail
    @Test
    fun `registerUser with mismatched passwords should fail`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password456"

        // ACT
        viewModel.registerUser(email, password, confirmPassword)

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Error)
            assertEquals("Passwords do not match", (state as AuthUiState.Error).message)
        }

        verify(mockRepository, never()).insertUser(any())
    }

    // Test 7 - Registration with short password should fail
    @Test
    fun `registerUser with short password should fail`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "pass"
        val confirmPassword = "pass"

        // ACT
        viewModel.registerUser(email, password, confirmPassword)

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Error)
            assertEquals("Password must be at least 8 characters long", (state as AuthUiState.Error).message)
        }

        verify(mockRepository, never()).insertUser(any())
    }

    // Test 8 - Registration should set loading state during operation
    @Test
    fun `registerUser should set loading state during operation`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val confirmPassword = "password123"

        // ACT
        viewModel.registerUser(email, password, confirmPassword)

        // ASSERT
        viewModel.authUiState.test {
            skipItems(1)
            val loadingState = awaitItem()
            assertTrue(loadingState is AuthUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 9 - Registration should update currentUser on success
    @Test
    fun `registerUser should update currentUser on success`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"

        // ACT
        viewModel.registerUser(email, password, password)
        advanceUntilIdle()

        // ASSERT
        viewModel.currentUser.test {
            val user = awaitItem()
            assertNotNull(user)
            assertEquals(email, user?.email)
            assertEquals(0.0, user?.balance ?: 0.0, 0.01)}
    }

    /*
        LOGIN TESTS
     */

    // Test 10 - Login with valid credentials should succeed
    @Test
    fun `loginUser with valid credentials should succeed`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val mockUser = User(1, email, password, 100.0)

        // Mock repository to return user
        whenever(mockRepository.authenticateUser(email, password)).thenReturn(mockUser)

        // ACT
        viewModel.loginUser(email, password)
        advanceUntilIdle()

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.LoginSuccess)
            assertEquals(mockUser, (state as AuthUiState.LoginSuccess).user)
        }

        // Verify repository was called
        verify(mockRepository, times(1)).authenticateUser(email, password)
    }

    // Test 11 - Login with invalid credentials should fail
    @Test
    fun `loginUser with invalid credentials should fail`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "wrongpassword"

        // Mock repository to return null (invalid credentials)
        whenever(mockRepository.authenticateUser(email, password)).thenReturn(null)

        // ACT
        viewModel.loginUser(email, password)
        advanceUntilIdle()

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Error)
            assertEquals("Invalid email or password", (state as AuthUiState.Error).message)
        }
    }

    // Test 12 - Login with empty email or password should fail
    @Test
    fun `loginUser with empty email should fail`() = runTest {
        // ARRANGE
        val email = ""
        val password = "password123"

        // ACT
        viewModel.loginUser(email, password)

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Error)
            assertEquals("Email cannot be empty", (state as AuthUiState.Error).message)
        }

        verify(mockRepository, never()).authenticateUser(anyString(), anyString())
    }

    // Test 13 - Login with empty password should fail
    @Test
    fun `loginUser with empty password should fail`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = ""

        // ACT
        viewModel.loginUser(email, password)

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Error)
            assertEquals("Password cannot be empty", (state as AuthUiState.Error).message)
        }

        verify(mockRepository, never()).authenticateUser(anyString(), anyString())
    }

    // Test 14 - Login should set loading state during operation
    @Test
    fun `loginUser should set loading state during operation`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val mockUser = User(1, email, password, 100.0)
        whenever(mockRepository.authenticateUser(email, password)).thenReturn(mockUser)

        // ACT
        viewModel.loginUser(email, password)

        // ASSERT
        viewModel.authUiState.test {
            skipItems(1) // Skip initial state
            val loadingState = awaitItem()
            assertTrue(loadingState is AuthUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 15 - Login should update currentUser on success
    @Test
    fun `loginUser should update currentUser on success`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val mockUser = User(1, email, password, 100.0)
        whenever(mockRepository.authenticateUser(email, password)).thenReturn(mockUser)

        // ACT
        viewModel.loginUser(email, password)
        advanceUntilIdle()

        // ASSERT
        viewModel.currentUser.test {
            val user = awaitItem()
            assertEquals(mockUser, user)
        }
    }

    /*
        LOGOUT TESTS
     */

    // Test 16 - Logout should clear currentUser
    @Test
    fun `logoutUser should clear currentUser`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val mockUser = User(1, email, password, 100.0)
        whenever(mockRepository.authenticateUser(email, password)).thenReturn(mockUser)
        viewModel.loginUser(email, password)
        advanceUntilIdle()

        // ACT
        viewModel.logoutUser()

        // ASSERT
        viewModel.currentUser.test {
            val user = awaitItem()
            assertNull(user)
        }
    }

    // Test 17 - Logout should reset authUiState to Idle
    @Test
    fun `logoutUser should reset authUiState to Idle`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val mockUser = User(1, email, password, 100.0)
        whenever(mockRepository.authenticateUser(email, password)).thenReturn(mockUser)
        viewModel.loginUser(email, password)
        advanceUntilIdle()

        // ACT
        viewModel.logoutUser()

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Idle)
        }
    }

    /*
    STATE RESET TESTS
     */

    // Test 18 - Resetting auth state should set state to Idle
    @Test
    fun `resetAuthState should set state to Idle`() = runTest {
        // ARRANGE
        viewModel.loginUser("", "")

        // ACT
        viewModel.resetAuthState()

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Idle)
        }
    }

    /*
        ERROR HANDLINGS TEST
     */

    // Test 19 - Registration should handle repository exception
    @Test
    fun `registerUser should handle repository exception`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val exceptionMessage = "Database error"

        whenever(mockRepository.insertUser(any())).thenThrow(RuntimeException(exceptionMessage))

        // ACT
        viewModel.registerUser(email, password, password)
        advanceUntilIdle()

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Error)
            assertTrue((state as AuthUiState.Error).message.contains("Registration failed"))
        }
    }

    // Test 20 - Login should handle repository exception
    @Test
    fun `loginUser should handle repository exception`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val password = "password123"
        val exceptionMessage = "Network error"

        whenever(mockRepository.authenticateUser(email, password))
            .thenThrow(RuntimeException(exceptionMessage))

        // ACT
        viewModel.loginUser(email, password)
        advanceUntilIdle()

        // ASSERT
        viewModel.authUiState.test {
            val state = awaitItem()
            assertTrue(state is AuthUiState.Error)
            assertTrue((state as AuthUiState.Error).message.contains("Login failed"))
        }
    }
}
