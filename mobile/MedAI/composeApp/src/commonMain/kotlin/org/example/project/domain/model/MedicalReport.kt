package org.example.project.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MedicalReport(
    val id: Int,
    val patientId: Int,
    val doctorId: Int? = null,
    val scanImageUrl: String? = null,
    val generatedReport: String,
    val status: String,
    val createdAt: String
)

@Serializable
data class CreateMedicalReportRequest(
    val scanData: String,
    val doctorId: Int? = null
)
