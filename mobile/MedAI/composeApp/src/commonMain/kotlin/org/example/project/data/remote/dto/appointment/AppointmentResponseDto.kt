package org.example.project.data.remote.dto.appointment

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.example.project.data.remote.dto.doctor.DoctorResponseDto
import org.example.project.data.remote.dto.patient.PatientResponseDto
import org.example.project.data.remote.dto.schedule.DocScheduleSlotResponseDto

@Serializable
data class AppointmentResponseDto(
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
    val doctor: DoctorResponseDto? = null,
    val patient: PatientResponseDto? = null,
    val scheduleSlot: DocScheduleSlotResponseDto? = null,
    val confirmedBy: JsonObject? = null,
)
