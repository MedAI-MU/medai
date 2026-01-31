package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val userId: String,
    val name: String,
    val role: String? = null
)
