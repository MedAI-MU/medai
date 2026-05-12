package org.example.project.data.remote.dto.doctor

import kotlinx.serialization.Serializable

@Serializable
data class DoctorResponseDto(
    val userId: Int,
    val name: String? = null,
    val specialities: List<DoctorSpecialityResponseDto> = emptyList()
)

@Serializable
data class DoctorSpecialityResponseDto(
    val id: Int,
    val isPrimary: Boolean,
    val yearsOfExperience: Int,
    val speciality: SpecialityItemResponseDto? = null
)

@Serializable
data class SpecialityItemResponseDto(
    val id: Int,
    val name: String
)
