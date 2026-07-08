package org.example.project.domain.model.diagnosis

data class Diagnosis(
    val id: Int,
    val patientUserId: Int,
    val doctorUserId: Int,
    val appointmentId: Int,
    val symptoms: String,
    val summary: String,
    val createdAt: String,
    val updatedAt: String
)

data class CreateDiagnosisParams(
    val appointmentId: Int,
    val symptoms: String,
    val summary: String
)

data class UpdateDiagnosisParams(
    val symptoms: String,
    val summary: String
)
