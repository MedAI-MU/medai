package org.example.project.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequestDto(
    val name: String? = null,
    val phone: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val bio: String? = null
)
