package org.example.project.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val birthDate: String? = null,
    val gender: String? = null,
    val role: String,
    val status: String,
    val avatar: String? = null,
    val bio: String? = null
)
