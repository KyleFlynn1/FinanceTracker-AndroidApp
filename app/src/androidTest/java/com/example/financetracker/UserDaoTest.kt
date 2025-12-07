package com.example.financetracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.financetracker.data.FinanceDatabase
import com.example.financetracker.data.User
import com.example.financetracker.data.UserDAO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

// Instrumentation tests for the UserDao
// Testing the room database and CRUD via the actual emulator rather than unit tests
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    // Setup database and userDao
    private lateinit var database: FinanceDatabase
    private lateinit var userDao: UserDAO

    // Before each test create an in-memory database
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            FinanceDatabase::class.java
        ).allowMainThreadQueries() // For testing only
            .build()

        userDao = database.userDao()
    }

    // After each test close the database
    @After
    fun tearDown() {
        database.close()
    }

    /*
        INSERT AND RECIEVE USER TESTS
     */

    // Test 1 - Inserting a user should add it to the database
    @Test
    fun insertUser_andRetrieveById() = runTest {
        // ARRANGE
        val user = User(
            id = 1,
            email = "test@example.com",
            password = "password123",
            balance = 100.0
        )

        // ACT
        userDao.insert(user)
        val retrievedUser = userDao.getUser(1).first()

        // ASSERT
        assertNotNull(retrievedUser)
        assertEquals("test@example.com", retrievedUser.email)
        assertEquals(100.0, retrievedUser.balance, 0.01)
    }

    // Test 2 - Inserting a user with a duplicate email should fail
    @Test
    fun insertUser_andRetrieveByEmail() = runTest {
        // ARRANGE
        val user = User(
            id = 1,
            email = "john@example.com",
            password = "password123",
            balance = 50.0
        )

        // ACT
        userDao.insert(user)
        val retrievedUser = userDao.getUserByEmail("john@example.com")

        // ASSERT
        assertNotNull(retrievedUser)
        assertEquals("john@example.com", retrievedUser.email)
        assertEquals(50.0, retrievedUser.balance, 0.01)
    }

    // Test 3 - Inserting a user with a null email should fail
    @Test
    fun getAllUsers_returnsAllInsertedUsers() = runTest {
        // ARRANGE
        val user1 = User(1, "alice@example.com", "pass1", 100.0)
        val user2 = User(2, "bob@example.com", "pass2", 200.0)

        // ACT
        userDao.insert(user1)
        userDao.insert(user2)
        val allUsers = userDao.getAllUsers().first()

        // ASSERT
        assertEquals(2, allUsers.size)
        assertTrue(allUsers.any { it.email == "alice@example.com" })
        assertTrue(allUsers.any { it.email == "bob@example.com" })
    }

    /*
        UPDATE USER TESTS
     */
    // Test 4 - Updating a user's balance should update the balance in the database
    @Test
    fun updateUser_changesUserData() = runTest {
        // ARRANGE
        val user = User(1, "test@example.com", "password", 100.0)
        userDao.insert(user)

        // ACT
        val updatedUser = user.copy(balance = 200.0)
        userDao.update(updatedUser)
        val result = userDao.getUser(1).first()

        // ASSERT
        assertEquals(200.0, result.balance, 0.01)
    }

    // Test 5 - Updating a user's balance should not affect other user data
    @Test
    fun updateUserBalance_updatesBalanceOnly() = runTest {
        // ARRANGE
        val user = User(1, "test@example.com", "password", 100.0)
        userDao.insert(user)

        // ACT
        userDao.updateUserBalance(userId = 1, newBalance = 300.0)
        val result = userDao.getUser(1).first()

        // ASSERT
        assertEquals(300.0, result.balance, 0.01)
        assertEquals("test@example.com", result.email)
    }

    /*
        DELETE USER TESTS
     */

    // Test 6 - Deleting a user should remove it from the database
    @Test
    fun deleteUser_removesUserFromDatabase() = runTest {
        // ARRANGE
        val user = User(1, "test@example.com", "password", 100.0)
        userDao.insert(user)

        // ACT
        userDao.delete(user)
        val allUsers = userDao.getAllUsers().first()

        // ASSERT
        assertTrue(allUsers.isEmpty())
    }

    /*
        VERIFICATION USER TESTS
     */

    // Test 7 - Verifying a user's balance should return true if the balance matches
    @Test
    fun getUserByCredentials_withValidCredentials_returnsUser() = runTest {
        // ARRANGE
        val user = User(1, "test@example.com", "password123", 100.0)
        userDao.insert(user)

        // ACT
        val authenticatedUser = userDao.getUserByCredentials(
            email = "test@example.com",
            password = "password123"
        )

        // ASSERT
        assertNotNull(authenticatedUser)
        assertEquals("test@example.com", authenticatedUser?.email)
    }

    // Test 8 - Verifying a user's balance should return false if the balance does not match
    @Test
    fun getUserByCredentials_withInvalidPassword_returnsNull() = runTest {
        // ARRANGE
        val user = User(1, "test@example.com", "password123", 100.0)
        userDao.insert(user)

        // ACT
        val authenticatedUser = userDao.getUserByCredentials(
            email = "test@example.com",
            password = "wrongpassword"
        )

        // ASSERT
        assertNull(authenticatedUser)
    }

    // Test 9 - Verifying a user's balance should return false if the user does not exist
    @Test
    fun getUserByCredentials_withNonexistentEmail_returnsNull() = runTest {
        // ACT
        val authenticatedUser = userDao.getUserByCredentials(
            email = "nonexistent@example.com",
            password = "password123"
        )

        // ASSERT
        assertNull(authenticatedUser)
    }

}