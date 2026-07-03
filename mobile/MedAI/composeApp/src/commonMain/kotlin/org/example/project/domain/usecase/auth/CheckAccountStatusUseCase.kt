package org.example.project.domain.usecase.auth

import org.example.project.domain.model.auth.AccountStatus
import org.example.project.domain.repository.auth.LoginRepository
import org.example.project.domain.repository.auth.UserSessionManager

class CheckAccountStatusUseCase(
    private val loginRepository: LoginRepository,
    private val sessionManager: UserSessionManager
) {
    /**
     * Re-authenticates the user to check their latest account status.
     * Required because the backend has no dedicated status-check endpoint.
     *
     * @param password The user's password (needed for re-authentication)
     * @return The latest AccountStatus from the backend
     */
    suspend operator fun invoke(password: String): Result<AccountStatus> {
        val email = sessionManager.getUserEmail()
            ?: return Result.failure(Exception("No active session"))

        val result = loginRepository.login(email, password)

        return result.map { authData ->
            // Update session with latest status
            sessionManager.updateAccountStatus(authData.accountStatus)
            authData.accountStatus
        }
    }
}
