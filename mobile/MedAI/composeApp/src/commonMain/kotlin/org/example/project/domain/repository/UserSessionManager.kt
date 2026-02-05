package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.example.project.domain.model.UserRole

interface UserSessionManager {
    suspend fun getUserId(): String?

    suspend fun getUserToken(): String?

    suspend fun getUserRole(): UserRole?

    suspend fun saveSession(userId: String, token: String, name: String, role: UserRole)

    suspend fun clearSession()

    val isUserLoggedIn: Flow<Boolean>
}
