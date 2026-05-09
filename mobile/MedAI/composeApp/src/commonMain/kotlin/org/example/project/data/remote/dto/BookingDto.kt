package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookingRequestDto(
    val slotId: Int,
    val doctorId: Int
)

@Serializable
data class BookingResponseDto(
    val id: Int,
    val patientUserId: Int,
    val doctorUserId: Int,
    val scheduleSlotId: Int,
    val status: String,
    val confirmedByUserId: Int? = null,
    val rating: Int? = null,
    val review: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
