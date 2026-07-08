package org.example.project.data.remote.dto.scans

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("patientUserId") val patientUserId: Int,
    @SerialName("appointmentId") val appointmentId: Int?,
    @SerialName("images") val images: List<ScanImageDto>,
    @SerialName("createdAt") val createdAt: String
)

@Serializable
data class ScanImageDto(
    @SerialName("id") val id: Int,
    @SerialName("path") val path: String
)
