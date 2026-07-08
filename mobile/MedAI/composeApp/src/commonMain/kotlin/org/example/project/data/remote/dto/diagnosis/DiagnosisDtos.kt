package org.example.project.data.remote.dto.diagnosis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiagnosisResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("patientUserId") val patientUserId: Int,
    @SerialName("doctorUserId") val doctorUserId: Int,
    @SerialName("appointmentId") val appointmentId: Int,
    @SerialName("symptoms") val symptoms: String,
    @SerialName("summary") val summary: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
data class CreateDiagnosisRequestDto(
    @SerialName("appointmentId") val appointmentId: Int,
    @SerialName("symptoms") val symptoms: String,
    @SerialName("summary") val summary: String
)

@Serializable
data class UpdateDiagnosisRequestDto(
    @SerialName("symptoms") val symptoms: String,
    @SerialName("summary") val summary: String
)
