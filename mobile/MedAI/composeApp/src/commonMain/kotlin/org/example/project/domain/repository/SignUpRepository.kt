package org.example.project.domain.repository

import org.example.project.data.remote.dto.SignUpRequest
import org.example.project.data.remote.dto.SignUpResponse

interface SignUpRepository {
    suspend fun register(request: SignUpRequest): Result<SignUpResponse>
}
