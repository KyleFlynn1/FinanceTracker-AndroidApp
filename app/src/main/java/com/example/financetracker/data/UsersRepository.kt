package com.example.financetracker.data

import kotlinx.coroutines.flow.Flow


// Repo that provides insert update and delete and retrieve of [USER] from a given source

interface UsersRepository {
    // Retrieve all the users from the given data source
    fun getAllUsersStream(): Flow<List<User>>

    // Retrieve a user from the given data source that matches with the [id]
    fun getUserStream(id: Int): Flow<User?>

    // Get a user by email
    fun getUserByEmail(email: String): User?


    // Insert user in the data source
    suspend fun insertUser(user: User)

    // Delete user from the data source
    suspend fun deleteUser(user: User)

    // Update user in the data source
    suspend fun updateUser(user: User)

    // Authenticate user with the email and password
    suspend fun authenticateUser(email: String, password: String): User?

    // Update balancce from transactonis
    suspend fun updateUserBalance(userId: Int, newBalance: Double)

}