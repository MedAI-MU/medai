package org.example.project.domain.repository.auth

import org.example.project.domain.model.auth.AuthResult
import org.example.project.domain.model.auth.RegisterRequest

interface SignUpRepository {
    suspend fun register(request: RegisterRequest): Result<AuthResult>
}
