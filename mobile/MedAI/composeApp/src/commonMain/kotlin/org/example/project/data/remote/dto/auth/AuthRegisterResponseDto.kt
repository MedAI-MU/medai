package org.example.project.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * Mobile-internal DTO representing the result of a successful registration + auto-login flow.
 * Not a direct backend response — assembled by the repository.
 */
@Serializable
data class AuthRegisterResponseDto(
    val token: String,
    val userId: String,
    val role: String,
    val message: String
)
