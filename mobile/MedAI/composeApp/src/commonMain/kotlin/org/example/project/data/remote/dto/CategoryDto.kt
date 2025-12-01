package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val name: String, // Display name from backend (optional fallback)
    @SerialName("icon_key") val iconKey: String // Key to map to local icon/string resources
)
