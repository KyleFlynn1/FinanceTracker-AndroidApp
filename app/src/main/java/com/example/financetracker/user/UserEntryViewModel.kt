package com.example.financetracker.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.financetracker.data.User
import com.example.financetracker.data.UsersRepository
import java.text.NumberFormat

// ViewModel to validate and insert items in the Room Database
class UserEntryViewModel(private val usersRepository: UsersRepository) : ViewModel() {
    // Holds Current Item UI State
    var userUiState by mutableStateOf(UserUiState())
        private set

    //Update the [userUiState] with the value provided in the argumment. This method also triggers aa validation for input values
    fun updateUserUiState(userDetails: UserDetails) {
        userUiState =
            UserUiState(userDetails = userDetails, isEntryValid = validateInput(userDetails))
    }

    suspend fun saveUser() {
        if (validateInput()) {
            usersRepository.insertUser(userUiState.userDetails.toUser())
        }
    }

    private fun validateInput(uiState: UserDetails = userUiState.userDetails): Boolean {
        return with(uiState) {
            email.isNotBlank() && password.isNotBlank()
        }
    }
}

data class UserUiState(
    val userDetails: UserDetails = UserDetails(),
    val isEntryValid: Boolean = false
)

data class UserDetails(
    val id: Int = 0,
    val email: String = "",
    val password: String = "",
    val balance: Double = 0.0
)

/*
    Extension function to convert [UserDetails] to [User].
 */

fun UserDetails.toUser(): User = User(
    id = id,
    email = email,
    password = password,
    balance = balance
)

// Extension function to convert [ User] to [UserUiState]
fun User.toUserUiState(isEntryValid: Boolean = false): UserUiState = UserUiState(
    userDetails = this.toUserDetails(),
    isEntryValid = isEntryValid
)

// Extension function to convert [User] to [UserDetails]
fun User.toUserDetails(): UserDetails = UserDetails(
    id = id,
    email = email,
    password = password,
    balance = balance
)
