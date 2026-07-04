package org.example.project.data.remote.dto.doctor

import kotlinx.serialization.Serializable

@Serializable
data class AssignSpecialityRequestDto(
    val specialityId: Int,
    val isPrimary: Boolean,
    val yearsOfExperience: Int
)
