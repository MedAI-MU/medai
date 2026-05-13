package org.example.project.data.remote.dto.appointment

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

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
    val doctor: JsonObject? = null,
    val patient: JsonObject? = null,
    val scheduleSlot: JsonObject? = null,
    val confirmedBy: JsonObject? = null,
)
