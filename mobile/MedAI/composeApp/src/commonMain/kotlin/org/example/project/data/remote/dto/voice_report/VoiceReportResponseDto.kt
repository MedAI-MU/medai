package org.example.project.data.remote.dto.voice_report

import kotlinx.serialization.Serializable

@Serializable
data class VoiceReportResponseDto(
    val id: Int,
    val status: String,
    val patientUserId: Int,
    val doctorUserId: Int,
    val appointmentId: Int? = null,
    val clinicalReport: ClinicalReportDto? = null,
    val transcription: String? = null,
    val audioUrl: String? = null,
    val errorMessage: String? = null,
    val createdAt: String,
    val updatedAt: String? = null
)

@Serializable
data class ClinicalReportDto(
    val symptoms: List<ClinicalSymptomDto> = emptyList(),
    val systemic_summary: String = ""
)

@Serializable
data class ClinicalSymptomDto(
    val description_ar: String,
    val clinical_term: String
)
