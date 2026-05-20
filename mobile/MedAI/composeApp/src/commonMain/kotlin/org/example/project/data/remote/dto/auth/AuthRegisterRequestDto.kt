package org.example.project.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val role: String
)
