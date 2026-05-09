package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(
    val id: Int,
    val name: String,
    val email: String? = null,
    val role: String? = null
)
