package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DoctorDto(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Double = 0.0,
    @SerialName("image_url") val imageUrl: String? = null,
    val bio: String? = null,
    @SerialName("review_count") val reviewCount: Int? = null
)
