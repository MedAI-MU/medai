package org.example.project.domain.repository.auth

import org.example.project.domain.model.auth.AuthResult

interface LoginRepository {
    suspend fun login(email: String, password: String): Result<AuthResult>
}
