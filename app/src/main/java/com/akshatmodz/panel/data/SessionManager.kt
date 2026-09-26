package com.akshatmodz.panel.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                "remembered_login",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e("SessionManager", "Failed to initialize EncryptedSharedPreferences", e)
            context.getSharedPreferences("remembered_login_fallback", Context.MODE_PRIVATE)
        }
    }

    companion object {
        private const val REMEMBER_EMAIL = "email"
        private const val REMEMBER_PASSWORD = "password"
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val USERLEVEL_KEY = stringPreferencesKey("userlevel")
        private val FULLNAME_KEY = stringPreferencesKey("fullname")
        private val EMAIL_KEY = stringPreferencesKey("email")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val usernameFlow: Flow<String?> = context.dataStore.data.map { it[USERNAME_KEY] }
    val userLevelFlow: Flow<String?> = context.dataStore.data.map { it[USERLEVEL_KEY] }

    suspend fun saveSession(token: String, username: String, level: String, fullname: String, email: String) {
        context.dataStore.edit {
            it[TOKEN_KEY] = token
            it[USERNAME_KEY] = username
            it[USERLEVEL_KEY] = level
            it[FULLNAME_KEY] = fullname
            it[EMAIL_KEY] = email
        }
    }

    suspend fun getToken(): String? = context.dataStore.data.first()[TOKEN_KEY]
    suspend fun getUsername(): String? = context.dataStore.data.first()[USERNAME_KEY]
    suspend fun getUserLevel(): String? = context.dataStore.data.first()[USERLEVEL_KEY]
    suspend fun getFullname(): String? = context.dataStore.data.first()[FULLNAME_KEY]

    suspend fun saveFullname(fullname: String) {
        context.dataStore.edit { it[FULLNAME_KEY] = fullname }
    }

    fun saveRememberedCredentials(email: String, password: String) {
        prefs.edit()
            .putString(REMEMBER_EMAIL, email)
            .putString(REMEMBER_PASSWORD, password)
            .apply()
    }

    fun clearRememberedCredentials() {
        prefs.edit()
            .remove(REMEMBER_EMAIL)
            .remove(REMEMBER_PASSWORD)
            .apply()
    }

    fun getRememberedEmail(): String {
        return prefs.getString(REMEMBER_EMAIL, "").orEmpty()
    }

    fun getRememberedPassword(): String {
        return prefs.getString(REMEMBER_PASSWORD, "").orEmpty()
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
