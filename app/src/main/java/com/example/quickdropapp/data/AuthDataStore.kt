package com.example.quickdropapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object AuthDataStore {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val USER_ID = intPreferencesKey("user_id")
    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")

    suspend fun saveAuthData(context: Context, userId: Int, accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun updateAccessToken(context: Context, accessToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun updateRefreshToken(context: Context, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearAuthData(context: Context) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences.remove(USER_ID)
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    fun isLoggedIn(context: Context): Boolean = runBlocking {
        context.dataStore.data.first()[IS_LOGGED_IN] ?: false
    }

    fun getUserId(context: Context): Int? = runBlocking {
        context.dataStore.data.first()[USER_ID]
    }

    fun getAccessToken(context: Context): String? = runBlocking {
        context.dataStore.data.first()[ACCESS_TOKEN]
    }

    fun getRefreshToken(context: Context): String? = runBlocking {
        context.dataStore.data.first()[REFRESH_TOKEN]
    }
}