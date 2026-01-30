package org.example.project.domain.repository

import org.example.project.data.remote.dto.AuthResult

interface LoginRepository {
    suspend fun login(email: String, password: String): Result<AuthResult>
}
