package com.example.financetracker.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.User
import com.example.financetracker.data.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Viewmodel to handle user authentication on login and registration
class UserViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    // Ui state for user authentication
    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    // Currently logged in user saved
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    //Register a new user
    fun registerUser(email: String, password: String, confirmPassword: String) {
        // Validate all inputs
        if (email.isBlank()) {
            _authUiState.value = AuthUiState.Error("Email cannot be empty")
            return
        }

        if (password.isBlank()) {
            _authUiState.value = AuthUiState.Error("Password cannot be empty")
            return
        }
        if (password != confirmPassword) {
            _authUiState.value = AuthUiState.Error("Passwords do not match")
            return
        }

        if (password.length < 8) {
            _authUiState.value = AuthUiState.Error("Password must be at least 8 characters long")
            return
        }

        // Create the user and save it
        viewModelScope.launch {
            try {
                _authUiState.value = AuthUiState.Loading

                // Id auto generated and the balance is 0.0 until transactions add and change it
                val newUser = User(
                    id = 0,
                    email = email,
                    password = password,
                    balance = 0.0
                )

                usersRepository.insertUser(newUser)
                _currentUser.value = newUser
                _authUiState.value = AuthUiState.RegistrationSuccess(newUser)

            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Registration failed: ${e.message}")
            }
        }
    }

    // Login to a user
    fun loginUser(email: String, password: String) {
        // Validate all inputs
        if (email.isBlank()) {
            _authUiState.value = AuthUiState.Error("Email cannot be empty")
            return
        }
        if (password.isBlank()) {
            _authUiState.value = AuthUiState.Error("Password cannot be empty")
            return
        }
        // Authenticate user
        viewModelScope.launch {
            try {
                _authUiState.value = AuthUiState.Loading
                val user = usersRepository.authenticateUser(email, password)

                if (user != null) {
                    _currentUser.value = user
                    _authUiState.value = AuthUiState.LoginSuccess(user)
                } else {
                    _authUiState.value = AuthUiState.Error("Invalid email or password")
                }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error("Login failed: ${e.message}")
            }
        }
    }

    // Loogout of curerntUser
    fun logoutUser() {
        _currentUser.value = null
        _authUiState.value = AuthUiState.Idle
    }

    // Reset the auth state
    fun resetAuthState() {
        _authUiState.value = AuthUiState.Idle
    }
}

// Ui state for authenticaiton operations
sealed class AuthUiState{
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class RegistrationSuccess(val user: User) : AuthUiState()
    data class LoginSuccess(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}