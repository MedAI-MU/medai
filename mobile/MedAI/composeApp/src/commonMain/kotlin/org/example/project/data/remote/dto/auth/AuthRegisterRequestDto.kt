package org.example.project.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * Maps to backend RegisterDto — the request body of POST /users.
 * Fields: name, email, password, phone, role.
 */
@Serializable
data class AuthRegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val role: String
)
