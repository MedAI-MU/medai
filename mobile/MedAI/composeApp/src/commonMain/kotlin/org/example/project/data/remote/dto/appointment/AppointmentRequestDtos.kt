package org.example.project.data.remote.dto.appointment

import kotlinx.serialization.Serializable

/**
 * Maps to backend CreateAppointmentDto — request body for POST /appointments.
 */
@Serializable
data class BookingRequestDto(
    val slotId: Int,
    val doctorId: Int
)

/**
 * Maps to backend UpdateAppointmentStatusDto — request body for PATCH /appointments/:id/status.
 */
@Serializable
data class UpdateAppointmentStatusRequestDto(
    val status: String
)

/**
 * Maps to backend ReviewAppointmentDto — request body for PATCH /appointments/:id/review.
 */
@Serializable
data class ReviewAppointmentRequestDto(
    val rating: Int,
    val review: String? = null
)
