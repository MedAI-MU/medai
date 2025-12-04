package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimeSlotDto(
    val id: String,
    val time: String,
    val isAvailable: Boolean
)
