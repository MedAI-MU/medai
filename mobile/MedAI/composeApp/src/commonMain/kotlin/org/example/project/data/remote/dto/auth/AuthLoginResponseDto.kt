package org.example.project.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthLoginResponseDto(
    val id: Int,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val role: String? = null,
    val status: String? = null
)
