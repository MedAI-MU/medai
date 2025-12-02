package org.example.project.domain.repository

interface SetPasswordRepository {
    suspend fun setPassword(password: String): Result<Unit>
}
