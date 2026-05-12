package org.example.project.data.remote.dto.doctor

import kotlinx.serialization.Serializable

@Serializable
data class SearchSpecialityRequestDto(
    val name: String
)
