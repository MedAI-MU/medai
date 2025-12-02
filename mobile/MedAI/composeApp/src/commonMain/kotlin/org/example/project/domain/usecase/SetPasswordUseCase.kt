package org.example.project.domain.usecase

import org.example.project.domain.repository.SetPasswordRepository


class SetPasswordUseCase(
    private val repository: SetPasswordRepository
) {
    suspend operator fun invoke(password: String, confirmPassword: String): Result<Unit> {
        // 1. Validation Rules
        if (password.isBlank()) {
            return Result.failure(Exception("Password cannot be empty"))
        }

        if (password.length < 8) {
            return Result.failure(Exception("Password must be at least 8 characters"))
        }

        if (password != confirmPassword) {
            return Result.failure(Exception("Passwords do not match"))
        }

        // 2. If valid, call the repository
        return repository.setPassword(password)
    }
}
