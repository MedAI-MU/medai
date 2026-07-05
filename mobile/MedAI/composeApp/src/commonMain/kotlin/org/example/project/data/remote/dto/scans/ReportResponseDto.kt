package org.example.project.data.remote.dto.scans

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("patientUserId") val patientUserId: Int,
    @SerialName("scanId") val scanId: Int?,
    @SerialName("path") val path: String,
    @SerialName("createdAt") val createdAt: String
)
