package org.example.project.data.remote.dto.report_analysis

import kotlinx.serialization.Serializable

@Serializable
data class ReportAnalysisResponseDto(
    val id: Int,
    val patientUserId: Int,
    val scanId: Int? = null,
    val appointmentId: Int? = null,
    val path: String,
    val analysisStatus: String? = null,
    val analysisResult: AnalysisResultDto? = null,
    val analysisError: String? = null,
    val createdAt: String
)

@Serializable
data class AnalysisResultDto(
    val patient_metadata: PatientMetadataDto? = null,
    val key_findings: List<KeyFindingDto> = emptyList(),
    val overall_summary_ar: String? = null,
    val safety_note_ar: String? = null
)

@Serializable
data class PatientMetadataDto(
    val name: String? = null,
    val age: String? = null,
    val gender: String? = null,
    val report_date: String? = null
)

@Serializable
data class KeyFindingDto(
    val test_name: String,
    val measured_value: String,
    val unit: String,
    val reference_range: String? = null,
    val status_ar: String,
    val patient_explanation_ar: String
)
