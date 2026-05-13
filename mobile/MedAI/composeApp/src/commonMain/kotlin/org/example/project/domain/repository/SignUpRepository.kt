package org.example.project.domain.repository

import org.example.project.data.remote.dto.auth.AuthRegisterRequestDto
import org.example.project.data.remote.dto.auth.AuthRegisterResponseDto

interface SignUpRepository {
    suspend fun register(request: AuthRegisterRequestDto): Result<AuthRegisterResponseDto>
}
