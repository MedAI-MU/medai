package org.example.project.presentation.shared.diagnosis

import org.example.project.domain.model.diagnosis.Diagnosis
import org.example.project.domain.model.auth.UserRole

data class DiagnosisState(
    val isLoading: Boolean = false,
    val diagnoses: List<Diagnosis> = emptyList(),
    val error: String? = null,
    val userRole: UserRole? = null,
    val currentUserId: String? = null,
    val latestAppointmentId: Int? = null
)

sealed interface DiagnosisEvent {
    object LoadDiagnoses : DiagnosisEvent
    data class CreateDiagnosis(val appointmentId: Int? = null, val symptoms: String, val summary: String) : DiagnosisEvent
    data class UpdateDiagnosis(val diagnosisId: String, val symptoms: String, val summary: String) : DiagnosisEvent
    data class DeleteDiagnosis(val diagnosisId: String) : DiagnosisEvent
}

sealed interface DiagnosisEffect {
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : DiagnosisEffect
}
