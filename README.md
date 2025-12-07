# **Finance Tracker Android Application**
A personal finance tracker or spending budget app as part of Assignment 2 of Mobile App Dev

# Features
**All Screens**
- Login Screen
- Sign Up Screen
- Dashboard Screen
- Transaction Screen
- Add Transaction Screen
- Edit Transaction Screen
- Settings Screen

**UI and Design**
- Material 3
- [Figma Prototype](https://www.figma.com/proto/gEtb9DtjlRubUKEXmLAifz/Finance-Tracker?node-id=0-1&t=8UFKIoPAT3Zr1PdJ-1)

**State Management**

- View Models
- StateFlow 

**Data Persistence**

Room Database
- Users Stored
- Transactions Stored

DataStore
- User Settings (DarkMode/Notifications)

**Work Manager**

Scheduled Daily Notifications of the amount spent by the user

# **Testing**

***Instrumentation Tests***

| Dashboard Screen| Description |
|--|--|
| Test 1 | Should display the correct title|
| Test 2 | Should display the correct total balance|
| Test 3 | Should display the correct add button|
| Test 4 | Should display the correct navigation buttons|
| Test 5 | Should display the correct recent transactions header|
| Test 6| Add button should trigger navigation when clicked |
| Test 7 | Dashboard button should trigger navigation when clicked|
| Test 8 | Settings button should trigger navigation when clicked|

-

| User Dao| Description |
|--|--|
| Test 1 | Inserting a user should add it to the database|
| Test 2 | Inserting a user with a duplicate email should fail|
| Test 3 | Inserting a user with a null email should fail|
| Test 4 | Updating a user's balance should update the balance in the database|
| Test 5 | Updating a user's balance should not affect other user data|
| Test 6| Deleting a user should remove it from the database |
| Test 7 | Verifying a user's balance should return true if the balance matches|
| Test 8 | Verifying a user's balance should return false if the balance does not match|
| Test 9 | Verifying a user's balance should return false if the user does not exist|

***Unit Tests***

| Transaction View Model | Description |
|--|--|
| Test 1 | Initial state set to IDLE|
| Test 2 | Initial transactions list should be empty|
| Test 3 | Add transaction with valid data should succeed|
| Test 4 | Add transaction with invalid amount should fail|
| Test 5 | Add transaction with empty type should fail|
| Test 6| Add transaction without logged in user should fail |
| Test 7 | Delete transaction should succeed for authorized user|
| Test 8 | Delete transaction should fail for unauthorized user|
| Test 9 | Calculate total income should sum all income transactions |
| Test 10 | Calculate total expenses should sum all expense transactions |
| Test 11 | Calculate balance should return income minus expenses |

-

| User View Model | Description |
|--|--|
| Test 1 | Initial state should be set to Idle|
| Test 2 | No user should be logged in at the beginning|
| Test 3 | Registration with valid data should succeed|
| Test 4 | Registration with empty email should fail|
| Test 5 | Registration with empty password should fail|
| Test 6| Registration with mismatched passwords should fail |
| Test 7 | Registration with short password should fail|
| Test 8 | Registration should set loading state during operation|
| Test 9 | Registration should update currentUser on success |
| Test 10 | Login with valid credentials should succeed |
| Test 11 | Login with invalid credentials should fail |
| Test 12 | Login with empty email or password should fail |
| Test 13 | Login with empty password should fail |
| Test 14 | Login should set loading state during operation |
| Test 15 | Login should update currentUser on success|
| Test 16 | Logout should clear currentUser|
| Test 17 | Logout should reset authUiState to Idle |
| Test 18 | Resetting auth state should set state to Idle |
| Test 19 | Registration should handle repository exception|
| Test 20 | Login should handle repository exception|
