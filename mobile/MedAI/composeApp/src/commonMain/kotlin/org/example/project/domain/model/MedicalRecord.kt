package org.example.project.domain.model

import kotlinx.datetime.LocalDate

data class PatientEntity(
    val id: String,
    val fullName: String,
    val gender: Gender,
    val age: Int,
    val weight: Double,
    val height: Double,
    val bloodType: BloodType
)

enum class Gender { Male, Female, Other }
enum class BloodType(val label: String) {
    A_POS("A+"), A_NEG("A-"),
    B_POS("B+"), B_NEG("B-"),
    AB_POS("AB+"), AB_NEG("AB-"),
    O_POS("O+"), O_NEG("O-"),
    UNKNOWN("Unknown")
}

data class AllergyEntity(
    val id: String,
    val name: String,
    val symptoms: String,
    val dateAdded: LocalDate
)

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
