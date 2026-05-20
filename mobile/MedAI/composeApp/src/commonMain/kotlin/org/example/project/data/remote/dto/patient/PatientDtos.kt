package org.example.project.data.remote.dto.patient

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.domain.model.patient.BloodType
import org.example.project.domain.model.patient.FamilyRelation
import org.example.project.domain.model.patient.Gender
import org.example.project.domain.model.patient.MaritalStatus


@Serializable
data class PatientResponseDto(
    @SerialName("userId") val userId: Int? = null,
    val name: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    @SerialName("bloodType") val bloodType: BloodType? = null,
    @SerialName("maritalStatus") val maritalStatus: MaritalStatus? = null,
    val allergies: List<AllergyResponseDto> = emptyList(),
    val chronicDiseases: List<ChronicDiseaseResponseDto> = emptyList(),
    val familyHistories: List<FamilyHistoryResponseDto> = emptyList(),
    val surgeries: List<SurgeryResponseDto> = emptyList(),
    val emergencyContacts: List<EmergencyContactResponseDto> = emptyList(),
)

// --- Response DTOs for sub-records ---

@Serializable
data class AllergyResponseDto(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null
)

@Serializable
data class ChronicDiseaseResponseDto(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val diagnosisDate: String? = null
)

@Serializable
data class FamilyHistoryResponseDto(
    val id: Int? = null,
    val relation: FamilyRelation? = null,
    val condition: String? = null,
    val notes: String? = null
)

@Serializable
data class SurgeryResponseDto(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val date: String? = null
)

@Serializable
data class EmergencyContactResponseDto(
    val id: Int? = null,
    val name: String? = null,
    val relation: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null
)

// --- Request DTOs for creating sub-records ---

@Serializable
data class CreateAllergyRequestDto(
    val name: String? = null,
    val description: String? = null
)

@Serializable
data class UpdateAllergyRequestDto(
    val name: String? = null,
    val description: String? = null
)

@Serializable
data class CreateChronicDiseaseRequestDto(
    val name: String? = null,
    val description: String? = null,
    val diagnosisDate: String? = null
)

@Serializable
data class UpdateChronicDiseaseRequestDto(
    val name: String? = null,
    val description: String? = null,
    val diagnosisDate: String? = null
)

@Serializable
data class CreateFamilyHistoryRequestDto(
    val relation: FamilyRelation? = null,
    val condition: String? = null,
    val notes: String? = null
)

@Serializable
data class UpdateFamilyHistoryRequestDto(
    val relation: FamilyRelation? = null,
    val condition: String? = null,
    val notes: String? = null
)

@Serializable
data class CreateSurgeryRequestDto(
    val name: String? = null,
    val description: String? = null,
    val date: String? = null
)

@Serializable
data class UpdateSurgeryRequestDto(
    val name: String? = null,
    val description: String? = null,
    val date: String? = null
)

@Serializable
data class CreateEmergencyContactRequestDto(
    val name: String? = null,
    val relation: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null
)

@Serializable
data class UpdateEmergencyContactRequestDto(
    val name: String? = null,
    val relation: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null
)

// --- Create / Update Patient ---

@Serializable
data class CreatePatientRequestDto(
    @SerialName("birthDate") val birthDate: String,
    val height: Double? = null,
    val weight: Double? = null,
    val gender: Gender? = null,
    @SerialName("bloodType") val bloodType: BloodType? = null,
    @SerialName("maritalStatus") val maritalStatus: MaritalStatus? = null
)

@Serializable
data class UpdatePatientRequestDto(
    val height: Double? = null,
    val weight: Double? = null,
    @SerialName("bloodType") val bloodType: BloodType? = null,
    @SerialName("maritalStatus") val maritalStatus: MaritalStatus? = null
)
