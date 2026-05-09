package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingRequestDto(
    @SerialName("doctor_id") val doctorId: String,
    @SerialName("slot_id") val slotId: String,
    val date: String,
    @SerialName("patient_name") val patientName: String,
    @SerialName("patient_age") val patientAge: String,
    @SerialName("patient_gender") val patientGender: String,
    val problem: String
)

@Serializable
data class BookingResponseDto(
    @SerialName("booking_id") val bookingId: String,
    val status: String
)
