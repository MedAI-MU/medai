package org.example.project.domain.model.appointment

import kotlinx.datetime.LocalDateTime

data class AppointmentDetail(
    val id: String,
    val patientId: String,
    val doctorName: String,
    val specialty: String,
    val doctorRating: Double,
    val date: LocalDateTime,
    val status: AppointmentDetailStatus,
    val patientName: String,
    val canRebook: Boolean,
    val canAddReview: Boolean,
    val rating: Int? = null,
    val review: String? = null
)

enum class AppointmentDetailStatus {
    UPCOMING,
    FINISHED,
    CANCELLED
}

data class CancelReason(
    val id: String,
    val reason: String
)
