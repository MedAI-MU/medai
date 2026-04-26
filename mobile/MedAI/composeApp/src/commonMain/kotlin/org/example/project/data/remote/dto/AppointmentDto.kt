package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentDto(
    val id: Int,
    val status: String,
    val createdAt: String,
    val bookedForName: String? = null,
    val bookedForAge: String? = null,
    val bookedForGender: String? = null,
    val problemDescription: String? = null,
    val cancellationReason: String? = null,
    val rating: Int? = null,
    val reviewComment: String? = null,
    val doctor: DoctorDto? = null,
    val patient: PatientDto? = null,
    val slot: org.example.project.data.remote.dto.schedule.DocScheduleSlotResponseDto? = null
)

@Serializable
data class UpdateAppointmentStatusRequestDto(
    val status: String,
    val cancellationReason: String? = null
)
