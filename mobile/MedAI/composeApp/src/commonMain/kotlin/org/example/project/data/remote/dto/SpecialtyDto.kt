package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecialtyDto(
    val id: String,
    val name: String,
    @SerialName("icon_key") val iconKey: String
)
