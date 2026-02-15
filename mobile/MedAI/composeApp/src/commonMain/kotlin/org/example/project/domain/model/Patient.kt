package org.example.project.domain.model

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
    val lastVisitDate: String? = null
)

@Serializable
enum class Gender {
    @SerialName("male") Male,
    @SerialName("female") Female,
    @SerialName("other") Other
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
