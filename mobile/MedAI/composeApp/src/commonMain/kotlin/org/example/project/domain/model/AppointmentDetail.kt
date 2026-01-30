package org.example.project.domain.model

import kotlinx.datetime.LocalDateTime

data class AppointmentDetail(
    val id: String,
    val doctorName: String,
    val specialty: String,
    val doctorRating: Double,
    val date: LocalDateTime,
    val status: AppointmentDetailStatus,
    val patientName: String,
    val patientAge: String,
    val patientGender: String,
    val problemDescription: String,
    val canRebook: Boolean,
    val canAddReview: Boolean
)

enum class AppointmentDetailStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
}

data class CancelReason(
    val id: String,
    val reason: String
)
