package org.example.project.domain.model.patient

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Patient(
    val id: String,
    val fullName: String,
    val gender: Gender,
    val age: Int,
    val birthDate: String? = null,
    val weight: Double,
    val height: Double,
    val bloodType: BloodType,
    val maritalStatus: MaritalStatus = MaritalStatus.Single,
    val contactNumber: String = "",
    val email: String = "",
    val address: String = "",
    val medicalHistorySummary: String? = null,
    val lastVisitDate: String? = null,
    val allergies: List<AllergyEntity> = emptyList(),
    val chronicDiseases: List<ChronicDiseaseEntity> = emptyList(),
    val familyHistories: List<FamilyHistoryEntity> = emptyList(),
    val surgeries: List<SurgeryEntity> = emptyList(),
    val emergencyContacts: List<EmergencyContactEntity> = emptyList(),
)

@Serializable
data class AllergyEntity(
    val id: String,
    val name: String,
    val symptoms: String,
    val dateAdded: String
)

@Serializable
data class ChronicDiseaseEntity(
    val id: String,
    val name: String,
    val description: String? = null,
    val diagnosisDate: String? = null
)

@Serializable
data class FamilyHistoryEntity(
    val id: String,
    val relation: FamilyRelation,
    val condition: String,
    val notes: String? = null
)

@Serializable
data class SurgeryEntity(
    val id: String,
    val name: String,
    val description: String? = null,
    val date: String
)

@Serializable
data class EmergencyContactEntity(
    val id: String,
    val name: String,
    val relation: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val notes: String? = null
)

// --- Params for Repository Operations (to avoid DTO leakage) ---

data class CreatePatientParams(
    val birthDate: String,
    val height: Double? = null,
    val weight: Double? = null,
    val gender: Gender? = null,
    val bloodType: BloodType? = null,
    val maritalStatus: MaritalStatus? = null
)

data class UpdatePatientParams(
    val height: Double? = null,
    val weight: Double? = null,
    val bloodType: BloodType? = null,
    val maritalStatus: MaritalStatus? = null
)

data class AllergyParams(
    val name: String,
    val symptoms: String
)

data class ChronicDiseaseParams(
    val name: String,
    val description: String? = null,
    val diagnosisDate: String? = null
)

data class FamilyHistoryParams(
    val relation: FamilyRelation,
    val condition: String,
    val notes: String? = null
)

data class SurgeryParams(
    val name: String,
    val description: String? = null,
    val date: String
)

data class EmergencyContactParams(
    val name: String,
    val relation: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val notes: String? = null
)

@Serializable
enum class Gender {
    @SerialName("male") Male,
    @SerialName("female") Female,
}

@Serializable
enum class BloodType(val label: String) {
    @SerialName("A+") A_POS("A+"),
    @SerialName("A-") A_NEG("A-"),
    @SerialName("B+") B_POS("B+"),
    @SerialName("B-") B_NEG("B-"),
    @SerialName("AB+") AB_POS("AB+"),
    @SerialName("AB-") AB_NEG("AB-"),
    @SerialName("O+") O_POS("O+"),
    @SerialName("O-") O_NEG("O-"),
    @SerialName("unknown") UNKNOWN("Unknown")
}

@Serializable
enum class MaritalStatus {
    @SerialName("single") Single,
    @SerialName("married") Married,
    @SerialName("divorced") Divorced,
    @SerialName("widowed") Widowed
}

@Serializable
enum class FamilyRelation(val label: String) {
    @SerialName("father") Father("Father"),
    @SerialName("mother") Mother("Mother"),
    @SerialName("sibling") Sibling("Sibling"),
    @SerialName("child") Child("Child"),
    @SerialName("spouse") Spouse("Spouse"),
    @SerialName("other") Other("Other"),
    @SerialName("unknown") Unknown("Unknown")
}
