package org.example.project.data.remote.dto.appointment

import kotlinx.serialization.Serializable


@Serializable
data class BookingRequestDto(
    val slotId: Int,
    val doctorId: Int
)

@Serializable
data class UpdateAppointmentStatusRequestDto(
    val status: String
)

@Serializable
data class ReviewAppointmentRequestDto(
    val rating: Int,
    val review: String? = null
)
