package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DoctorDto(
    @SerialName("userId") val id: String,
    val user: UserDto? = null,
    val specialities: List<DoctorSpecialityDto> = emptyList(),
    // Keep these fallbacks for older mocks/endpoints if any, but backend returns `user` now.
    val name: String? = null,
    val specialty: String? = null,
    val rating: Double = 0.0,
    @SerialName("image_url") val imageUrl: String? = null,
    val bio: String? = null,
    @SerialName("review_count") val reviewCount: Int? = null
)

@Serializable
data class DoctorSpecialityDto(
    val id: Int,
    val isPrimary: Boolean,
    val yearsOfExperience: Int,
    val speciality: SpecialtyItemDto? = null
)

@Serializable
data class SpecialtyItemDto(
    val id: Int,
    val name: String
)
