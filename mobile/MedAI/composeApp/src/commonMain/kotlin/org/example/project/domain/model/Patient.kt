package org.example.project.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Patient(
    val id: String,
    val name: String,
    val age: Int,
    val gender: String, // "Male", "Female", "Other"
    val contactNumber: String,
    val email: String? = null,
    val address: String? = null,
    val medicalHistorySummary: String? = null,
    val lastVisitDate: String? = null // ISO Date String
)
