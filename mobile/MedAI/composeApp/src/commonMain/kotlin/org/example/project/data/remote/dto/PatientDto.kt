package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.domain.model.BloodType
import org.example.project.domain.model.FamilyRelation
import org.example.project.domain.model.Gender
import org.example.project.domain.model.MaritalStatus

@Serializable
data class PatientUserDto(
    val id: Int? = null,
    val name: String? = null,
    val birthDate: String? = null,
    val gender: Gender? = null
)

@Serializable
data class PatientDto(
    val name: String? = null,
    @SerialName("userId") val userId: Int? = null,
    val user: PatientUserDto? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val gender: Gender? = null,
    @SerialName("bloodType") val bloodType: BloodType? = null,
    @SerialName("maritalStatus") val maritalStatus: MaritalStatus? = null,
    val allergies: List<AllergyDto> = emptyList(),
    val chronicDiseases: List<ChronicDiseaseDto> = emptyList(),
    val familyHistories: List<FamilyHistoryDto> = emptyList(),
    val surgeries: List<SurgeryDto> = emptyList(),
    val emergencyContacts: List<EmergencyContactDto> = emptyList(),
)

@Serializable
data class AllergyDto(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null
)

@Serializable
data class UpdateAllergyDto(
    val name: String? = null,
    val description: String? = null
)

@Serializable
data class ChronicDiseaseDto(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val diagnosisDate: String? = null
)

@Serializable
data class UpdateChronicDiseaseDto(
    val name: String? = null,
    val description: String? = null,
    val diagnosisDate: String? = null
)

@Serializable
data class FamilyHistoryDto(
    val id: Int? = null,
    val relation: FamilyRelation? = null,
    val condition: String? = null,
    val notes: String? = null
)

@Serializable
data class UpdateFamilyHistoryDto(
    val relation: FamilyRelation? = null,
    val condition: String? = null,
    val notes: String? = null
)

@Serializable
data class SurgeryDto(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val date: String? = null
)

@Serializable
data class UpdateSurgeryDto(
    val name: String? = null,
    val description: String? = null,
    val date: String? = null
)

@Serializable
data class EmergencyContactDto(
    val id: Int? = null,
    val name: String? = null,
    val relation: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null
)

@Serializable
data class UpdateEmergencyContactDto(
    val name: String? = null,
    val relation: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null
)

@Serializable
data class AnalysisDto(
    val analysis_id: String,
    val type_name: String,
    val date_performed: String,
    val status_code: Int // 0: Pending, 1: Completed, 2: Cancelled
)

@Serializable
data class VaccinationDto(
    val vaccine_id: String,
    val vaccine_name: String,
    val admin_date: String,
    val next_dose: String?,
    val is_completed: Boolean
)

@Serializable
data class MedicalHistoryDto(
    val record_id: String,
    val condition: String,
    val current_status: String,
    val plan: String,
    val provider_name: String
)

@Serializable
data class CreatePatientDto(
    @SerialName("birthDate") val birthDate: String,
    val height: Double? = null,
    val weight: Double? = null,
    val gender: Gender? = null,
    @SerialName("bloodType") val bloodType: BloodType? = null,
    @SerialName("maritalStatus") val maritalStatus: MaritalStatus? = null
)

@Serializable
data class UpdatePatientDto(
    val height: Double? = null,
    val weight: Double? = null,
    @SerialName("bloodType") val bloodType: BloodType? = null,
    @SerialName("maritalStatus") val maritalStatus: MaritalStatus? = null
)
