package org.example.project.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResultDto(
    val userId: String,
    val token: String,
    val userName: String,
    val role: String,
    val email: String
)
