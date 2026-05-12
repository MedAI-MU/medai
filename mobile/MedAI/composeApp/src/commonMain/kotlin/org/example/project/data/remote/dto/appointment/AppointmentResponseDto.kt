package org.example.project.data.remote.dto.appointment

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Maps to backend AppointmentDto — the response from all appointment endpoints.
 * Merged from old AppointmentDto + BookingResponseDto (which were duplicates).
 *
 * The backend eagerly loads relations (doctor, patient, scheduleSlot) for some endpoints.
 * These come back as nested JSON objects. We capture them as optional JsonObject
 * so deserialization succeeds, even though we don't parse them into typed DTOs here.
 */
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
