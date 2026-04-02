package org.example.project.data.remote.dto.appointments

import kotlinx.serialization.Serializable

@Serializable
data class CreateAppointmentRequest(
    val doctorId: Int,
    val slotId: Int,
    val problemDescription: String? = null
)

@Serializable
data class CancelAppointmentRequest(
    val reasonId: String? = null,
    val otherReason: String? = null
)

@Serializable
data class AppointmentDto(
    val id: Int,
    val status: String,
    val problemDescription: String? = null,
    val cancelReason: String? = null,
    val rating: Int? = null,
    val reviewComment: String? = null,
    val createdAt: String,
    val patient: AppointmentPatientDto? = null,
    val doctor: AppointmentDoctorDto? = null,
    val slot: AppointmentSlotDto? = null
)

@Serializable
data class AppointmentPatientDto(
    val userId: Int,
    val height: Double? = null,
    val weight: Double? = null,
    val user: AppointmentUserDto? = null
)

@Serializable
data class AppointmentDoctorDto(
    val userId: Int,
    val id: Int? = null,
    val user: AppointmentUserDto? = null
)

@Serializable
data class AppointmentUserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val role: String,
    val dateOfBirth: String? = null,
    val gender: String? = null
)

@Serializable
data class AppointmentSlotDto(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val status: String,
    val schedule: AppointmentScheduleDto? = null
)

@Serializable
data class AppointmentScheduleDto(
    val id: Int,
    val dayDate: String
)
