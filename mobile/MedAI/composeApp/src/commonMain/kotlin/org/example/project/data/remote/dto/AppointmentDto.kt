package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentDto(
    val id: Int,
    val patientUserId: Int,
    val doctorUserId: Int,
    val scheduleSlotId: Int,
    val status: String,
    val confirmedByUserId: Int? = null,
    val rating: Int? = null,
    val review: String? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val doctor: DoctorDto? = null,
    val patient: PatientDto? = null,
    val scheduleSlot: org.example.project.data.remote.dto.schedule.DocScheduleSlotResponseDto? = null
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
