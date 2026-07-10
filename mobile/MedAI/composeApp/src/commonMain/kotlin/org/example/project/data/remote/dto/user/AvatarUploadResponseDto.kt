package org.example.project.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class AvatarUploadResponseDto(
    val avatar: String
)
