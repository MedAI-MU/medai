package org.example.project.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthLoginRequestDto(
    val email: String,
    val password: String
)
