package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PatientDto(
    val id: String,
    val full_name: String,
    val gender_code: String,
    val age: Int,
    val weight_kg: Double,
    val height_cm: Double,
    val blood_group: String
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
