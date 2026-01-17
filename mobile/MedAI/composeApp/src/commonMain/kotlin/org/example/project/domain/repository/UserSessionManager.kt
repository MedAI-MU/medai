package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserSessionManager {
    suspend fun getUserId(): String?

    suspend fun getUserToken(): String?

    suspend fun saveSession(userId: String, token: String, name: String)

    suspend fun clearSession()

    val isUserLoggedIn: Flow<Boolean>
}
