package org.example.project.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * Maps to the backend AuthenticatedUserDto — the response body of POST /auth/login.
 * Fields: id (number), name, email, phone, role.
 */
@Serializable
data class AuthLoginResponseDto(
    val id: Int,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val role: String? = null
)
