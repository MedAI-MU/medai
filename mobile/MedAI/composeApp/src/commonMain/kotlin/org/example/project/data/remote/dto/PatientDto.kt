package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.domain.model.BloodType
import org.example.project.domain.model.Gender
import org.example.project.domain.model.MaritalStatus

@Serializable
data class PatientDto(
    val id: String? = null,
    val name: String? = null,
    @SerialName("birthDate") val birthDate: String,
    val height: Double,
    val weight: Double,
    val gender: Gender,
    @SerialName("bloodType") val bloodType: BloodType,
    @SerialName("maritalStatus") val maritalStatus: MaritalStatus,
)

@Serializable
data class CreatePatientDto(
    @SerialName("birthDate") val birthDate: String,
    val height: Double,
    val weight: Double,
    val gender: Gender,
    @SerialName("bloodType") val bloodType: BloodType,
    @SerialName("maritalStatus") val maritalStatus: MaritalStatus
)

@Serializable
data class UpdatePatientDto(
    val height: Double,
    val weight: Double,
    @SerialName("bloodType") val bloodType: BloodType,
    @SerialName("maritalStatus") val maritalStatus: MaritalStatus
)

@Serializable
data class AllergyDto(
    val id: String,
    val allergen: String,
    val reaction: String,
    val detected_date: String
)

// 3. Analysis DTO
@Serializable
data class AnalysisDto(
    val analysis_id: String,
    val type_name: String,
    val date_performed: String,
    val status_code: Int // 0: Pending, 1: Completed, 2: Cancelled
)

// 4. Vaccination DTO
@Serializable
data class VaccinationDto(
    val vaccine_id: String,
    val vaccine_name: String,
    val admin_date: String,
    val next_dose: String?,
    val is_completed: Boolean
)

// 5. Medical History DTO
@Serializable
data class MedicalHistoryDto(
    val record_id: String,
    val condition: String,
    val current_status: String,
    val plan: String,
    val provider_name: String
)
