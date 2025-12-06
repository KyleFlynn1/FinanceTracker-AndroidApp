package com.example.financetracker.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val DAILY_NOTIFICATION = booleanPreferencesKey("daily_notification")
    }

    val darkMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("SettingsRepository", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DARK_MODE] ?: false
        }

    val dailyNotification: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("SettingsRepository", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DAILY_NOTIFICATION] ?: false
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }

    suspend fun setDailyNotification(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DAILY_NOTIFICATION] = enabled
        }
    }
}