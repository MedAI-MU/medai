package org.example.project.data.remote.dto.manager

import kotlinx.serialization.Serializable

@Serializable
data class ManagedUserDto(
    val id: Int,
    val name: String,
    val role: String? = null,
    val status: String? = null,
    val gender: String? = null
)

@Serializable
data class ApproveUserRequestDto(
    val userId: Int
)

@Serializable
data class DoctorManagerDto(
    val userId: Int,
    val name: String? = null
)
