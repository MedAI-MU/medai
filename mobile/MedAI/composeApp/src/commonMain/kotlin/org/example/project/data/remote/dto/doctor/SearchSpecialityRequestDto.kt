package org.example.project.data.remote.dto.doctor

import kotlinx.serialization.Serializable

/**
 * Maps to backend SpecialityDto — request body for POST /doctors/search/speciality.
 */
@Serializable
data class SearchSpecialityRequestDto(
    val name: String
)
