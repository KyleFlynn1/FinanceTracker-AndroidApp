package com.example.financetracker.data

import kotlinx.coroutines.flow.Flow

class OfflineUsersRepository(private val userDao: UserDAO) : UsersRepository {
    override fun getAllUsersStream(): Flow<List<User>> = userDao.getAllUsers()

    override fun getUserStream(id: Int): Flow<User?> = userDao.getUser(id)

    override fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    override suspend fun insertUser(user: User) = userDao.insert(user)

    override suspend fun deleteUser(user: User) = userDao.delete(user)

    override suspend fun updateUser(user: User) = userDao.update(user)

    override suspend fun authenticateUser(email: String, password: String): User? {
        return userDao.getUserByCredentials(email, password)
    }
    override suspend fun updateUserBalance(userId: Int, newBalance: Double) {
        userDao.updateUserBalance(userId, newBalance)
    }
}