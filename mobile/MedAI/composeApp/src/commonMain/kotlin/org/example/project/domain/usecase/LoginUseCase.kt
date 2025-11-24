package org.example.project.domain.usecase

import org.example.project.domain.repository.LoginRepository

class LoginUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and Password cannot be empty"))
        }
        // we have to hash the password here before sending it to the repository
        // if the backend requires it (though usually HTTPS + Cleartext is standard).
        return repository.login(email, password)
    }
}
