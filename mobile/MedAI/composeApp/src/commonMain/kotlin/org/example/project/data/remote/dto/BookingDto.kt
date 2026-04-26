package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingRequestDto(
    val doctorId: Int,
    val slotId: Int,
    val bookedForName: String? = null,
    val bookedForAge: String? = null,
    val bookedForGender: String? = null,
    val problemDescription: String? = null
)

@Serializable
data class BookingResponseDto(
    val id: Int,
    val status: String
)
