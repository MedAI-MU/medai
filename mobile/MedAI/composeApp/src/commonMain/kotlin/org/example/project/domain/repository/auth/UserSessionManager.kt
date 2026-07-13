package org.example.project.domain.repository.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.model.auth.AccountStatus

interface UserSessionManager {
    suspend fun getUserId(): String?

    suspend fun getUserName(): String?

    suspend fun getUserEmail(): String?

    suspend fun getUserToken(): String?

    suspend fun getUserRole(): UserRole?

    suspend fun getAccountStatus(): AccountStatus?

    suspend fun updateAccountStatus(status: AccountStatus)

    suspend fun saveSession(
        userId: String,
        token: String,
        name: String,
        email: String,
        role: UserRole,
        accountStatus: AccountStatus = AccountStatus.APPROVED
    )

    suspend fun getCookies(): Set<String>

    suspend fun saveCookies(cookies: Set<String>)

    suspend fun updateUserToken(token: String)

    suspend fun clearSession()

    val isUserLoggedIn: Flow<Boolean>
}
