package org.example.project.domain.usecase.auth

import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.repository.auth.LoginRepository
import org.example.project.domain.repository.auth.UserSessionManager

class LoginUseCase(
    private val repository: LoginRepository,
    private val sessionManager: UserSessionManager
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and Password cannot be empty"))
        }
        // we have to hash the password here before sending it to the repository
        // if the backend requires it (though usually HTTPS + Cleartext is standard).
        val result = repository.login(email, password)

        // 2. If Success -> Save to Session Manager
        return result.map { authData ->
            sessionManager.saveSession(
                userId = authData.userId,
                token = authData.token,
                name = authData.userName,
                email = authData.email,
                role = UserRole.valueOf(authData.role.uppercase())
            )
        }
    }
}
