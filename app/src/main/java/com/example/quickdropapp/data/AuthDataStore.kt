// AuthDataStore.kt
package com.example.quickdropapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object AuthDataStore {
    // Definieer de DataStore met de preferencesDataStore-delegate
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val USER_ID = intPreferencesKey("user_id")

    suspend fun saveAuthData(context: Context, userId: Int) {
        context.dataStore.edit { preferences: MutablePreferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId
        }
    }

    suspend fun clearAuthData(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            preferences[IS_LOGGED_IN] = false
            preferences.remove(USER_ID)
        }
    }

    fun isLoggedIn(context: Context): Boolean = runBlocking {
        val preferences = context.dataStore.data.first()
        preferences[IS_LOGGED_IN] ?: false
    }

    fun getUserId(context: Context): Int? = runBlocking {
        val preferences = context.dataStore.data.first()
        preferences[USER_ID]
    }
}