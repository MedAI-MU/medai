package org.example.project.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRegisterResponseDto(
    val token: String,
    val userId: String,
    val role: String,
    val message: String
)
