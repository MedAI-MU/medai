package org.example.project.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.example.project.domain.repository.UserSessionManager
import org.example.project.domain.model.UserRole

class InMemoryUserSessionManager(
    private val dataStore: DataStore<Preferences>
) : UserSessionManager {

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_TOKEN = stringPreferencesKey("user_token")
        private val KEY_NAME = stringPreferencesKey("user_name")
        private val KEY_ROLE = stringPreferencesKey("user_role")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_COOKIES = stringPreferencesKey("cookies")
    }

    override val isUserLoggedIn: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    override suspend fun getUserId(): String? {
        return dataStore.data.first()[KEY_USER_ID]
    }

    override suspend fun getUserName(): String? {
        return dataStore.data.first()[KEY_NAME]
    }

    override suspend fun getUserToken(): String? {
        return dataStore.data.first()[KEY_TOKEN]
    }

    override suspend fun getUserRole(): UserRole? {
        val roleString = dataStore.data.first()[KEY_ROLE]
        return roleString?.let {
             try {
                 UserRole.valueOf(it)
             } catch (e: IllegalArgumentException) {
                 null
             }
        }
    }

    override suspend fun getCookies(): Set<String> {
         // Storing as a delimited string for simplicity, or JSON
         val cookiesString = dataStore.data.first()[KEY_COOKIES]
         return cookiesString?.split("|")?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
    }

    override suspend fun saveCookies(cookies: Set<String>) {
        dataStore.edit { prefs ->
            prefs[KEY_COOKIES] = cookies.joinToString("|")
        }
    }


    override suspend fun saveSession(userId: String, token: String, name: String, role: UserRole) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
            prefs[KEY_TOKEN] = token
            prefs[KEY_NAME] = name
            prefs[KEY_ROLE] = role.name
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
