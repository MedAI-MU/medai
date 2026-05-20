package org.example.project.domain.model.medical_record

import kotlinx.datetime.LocalDate

// Enums moved to Patient.kt

data class AnalysisEntity(
    val id: String,
    val type: String,
    val date: LocalDate,
    val status: AnalysisStatus
)

enum class AnalysisStatus { Completed, Pending, Cancelled }

data class AnalysisDetailEntity(
    val id: String,
    val analysisId: String,
    val parameter: String, // e.g., "Glucose"
    val value: String,
    val unit: String?,
    val referenceRange: String?
)

data class VaccinationEntity(
    val id: String,
    val name: String,
    val dateAdministered: LocalDate,
    val nextDoseDate: LocalDate?,
    val status: VaccinationStatus
)

enum class VaccinationStatus { Done, Scheduled, Overdue }

data class MedicalHistoryEntity(
    val id: String,
    val condition: String,
    val status: String, // e.g., "In Control"
    val treatmentPlan: String,
    val attendingDoctor: String
)
