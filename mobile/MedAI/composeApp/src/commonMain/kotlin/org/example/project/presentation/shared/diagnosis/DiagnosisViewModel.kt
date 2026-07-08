package org.example.project.presentation.shared.diagnosis

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.model.diagnosis.CreateDiagnosisParams
import org.example.project.domain.model.diagnosis.UpdateDiagnosisParams
import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.repository.appointment.AppointmentRepository
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.usecase.diagnosis.*

class DiagnosisViewModel(
    private val patientIdArg: String?,
    private val getPatientDiagnosesUseCase: GetPatientDiagnosesUseCase,
    private val createDiagnosisUseCase: CreateDiagnosisUseCase,
    private val updateDiagnosisUseCase: UpdateDiagnosisUseCase,
    private val deleteDiagnosisUseCase: DeleteDiagnosisUseCase,
    private val sessionManager: UserSessionManager,
    private val appointmentRepository: AppointmentRepository
) : MviScreenModel<DiagnosisState, DiagnosisEvent, DiagnosisEffect>(DiagnosisState()) {

    init {
        loadUserRoleAndId()
    }

    private fun loadUserRoleAndId() {
        screenModelScope.launch {
            val role = sessionManager.getUserRole()
            val userId = sessionManager.getUserId()
            setState { copy(userRole = role, currentUserId = userId) }
            if (role == UserRole.DOCTOR && patientIdArg != null) {
                fetchLatestAppointmentId(patientIdArg)
            }
            onEvent(DiagnosisEvent.LoadDiagnoses)
        }
    }

    private fun fetchLatestAppointmentId(patientId: String) {
        screenModelScope.launch {
            appointmentRepository.getMyAppointments()
                .onSuccess { appointments ->
                    val latestAppt = appointments
                        .filter { it.patientId == patientId }
                        .maxByOrNull { it.date }
                    if (latestAppt != null) {
                        setState { copy(latestAppointmentId = latestAppt.id.toIntOrNull()) }
                    }
                }
        }
    }

    override fun onEvent(event: DiagnosisEvent) {
        when (event) {
            is DiagnosisEvent.LoadDiagnoses -> loadDiagnoses()
            is DiagnosisEvent.CreateDiagnosis -> createDiagnosis(
                event.appointmentId ?: state.value.latestAppointmentId ?: 0,
                event.symptoms,
                event.summary
            )
            is DiagnosisEvent.UpdateDiagnosis -> updateDiagnosis(event.diagnosisId, event.symptoms, event.summary)
            is DiagnosisEvent.DeleteDiagnosis -> deleteDiagnosis(event.diagnosisId)
        }
    }

    private fun loadDiagnoses() {
        val patientId = patientIdArg ?: state.value.currentUserId ?: return

        screenModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getPatientDiagnosesUseCase(patientId)
                .onSuccess { diagnoses ->
                    setState { copy(isLoading = false, diagnoses = diagnoses) }
                }
                .onFailure { error ->
                    setState { copy(isLoading = false, error = error.message ?: "Failed to load diagnoses") }
                    sendEffect(DiagnosisEffect.ShowSnackbar(error.message ?: "Failed to load diagnoses", isError = true))
                }
        }
    }

    private fun createDiagnosis(appointmentId: Int, symptoms: String, summary: String) {
        val patientId = patientIdArg ?: return
        val params = CreateDiagnosisParams(appointmentId, symptoms, summary)

        screenModelScope.launch {
            setState { copy(isLoading = true) }
            createDiagnosisUseCase(patientId, params)
                .onSuccess {
                    setState { copy(isLoading = false) }
                    sendEffect(DiagnosisEffect.ShowSnackbar("Diagnosis created successfully"))
                    loadDiagnoses()
                }
                .onFailure { error ->
                    setState { copy(isLoading = false) }
                    sendEffect(DiagnosisEffect.ShowSnackbar(error.message ?: "Failed to create diagnosis", isError = true))
                }
        }
    }

    private fun updateDiagnosis(diagnosisId: String, symptoms: String, summary: String) {
        val patientId = patientIdArg ?: return
        val params = UpdateDiagnosisParams(symptoms, summary)

        screenModelScope.launch {
            setState { copy(isLoading = true) }
            updateDiagnosisUseCase(patientId, diagnosisId, params)
                .onSuccess {
                    setState { copy(isLoading = false) }
                    sendEffect(DiagnosisEffect.ShowSnackbar("Diagnosis updated successfully"))
                    loadDiagnoses()
                }
                .onFailure { error ->
                    setState { copy(isLoading = false) }
                    sendEffect(DiagnosisEffect.ShowSnackbar(error.message ?: "Failed to update diagnosis", isError = true))
                }
        }
    }

    private fun deleteDiagnosis(diagnosisId: String) {
        val patientId = patientIdArg ?: return

        screenModelScope.launch {
            setState { copy(isLoading = true) }
            deleteDiagnosisUseCase(patientId, diagnosisId)
                .onSuccess {
                    setState { copy(isLoading = false) }
                    sendEffect(DiagnosisEffect.ShowSnackbar("Diagnosis deleted successfully"))
                    loadDiagnoses()
                }
                .onFailure { error ->
                    setState { copy(isLoading = false) }
                    sendEffect(DiagnosisEffect.ShowSnackbar(error.message ?: "Failed to delete diagnosis", isError = true))
                }
        }
    }
}
