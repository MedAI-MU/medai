package org.example.project.presentation.recordScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.model.AllergyEntity
import org.example.project.domain.model.AnalysisEntity
import org.example.project.domain.model.MedicalHistoryEntity
import org.example.project.domain.model.Patient
import org.example.project.domain.model.VaccinationEntity
import org.example.project.domain.repository.UserSessionManager

import org.example.project.domain.usecase.GetAllergiesUseCase
import org.example.project.domain.usecase.GetAnalysesUseCase
import org.example.project.domain.usecase.GetMedicalHistoryUseCase
import org.example.project.domain.usecase.GetPatientProfileUseCase
import org.example.project.domain.usecase.GetVaccinationsUseCase
import org.example.project.domain.usecase.UpdatePatientMetricsUseCase

data class MedicalRecordState(
    val isLoading: Boolean = false,
    val patientProfile: Patient? = null,
    val allergies: List<AllergyEntity> = emptyList(),
    val analyses: List<AnalysisEntity> = emptyList(),
    val vaccinations: List<VaccinationEntity> = emptyList(),
    val medicalHistory: List<MedicalHistoryEntity> = emptyList(),
    val error: String? = null
)

// --- EVENTS (User Intents) ---
sealed class MedicalRecordEvent {
    object LoadFullRecords : MedicalRecordEvent()
    object Refresh : MedicalRecordEvent()

    // Updates
    data class UpdateBodyMetrics(val weight: Double, val height: Double) : MedicalRecordEvent()

    // Navigation Intents (To be handled by UI, but event triggered here)
    data class AnalysisClicked(val analysisId: String) : MedicalRecordEvent()
    object AddRecordClicked : MedicalRecordEvent()
}

// --- SIDE EFFECTS (One-off events) ---
sealed class MedicalRecordEffect {
    data class ShowError(val message: String) : MedicalRecordEffect()
    data class NavigateToAnalysisDetails(val analysisId: String) : MedicalRecordEffect()
    object NavigateToAddRecord : MedicalRecordEffect()
}

class MedicalRecordViewModel(
    private val getPatientProfileUseCase: GetPatientProfileUseCase,
    private val updatePatientMetricsUseCase: UpdatePatientMetricsUseCase,
    private val getAllergiesUseCase: GetAllergiesUseCase,
    private val getAnalysesUseCase: GetAnalysesUseCase,
    private val getVaccinationsUseCase: GetVaccinationsUseCase,
    private val getMedicalHistoryUseCase: GetMedicalHistoryUseCase,
    private val sessionManager: UserSessionManager
) : ScreenModel {
    private var currentPatientId = "p1"

    private val _state = MutableStateFlow(MedicalRecordState())
    val state = _state.asStateFlow()

    init {
        screenModelScope.launch {
            val id = sessionManager.getUserId()
            if (id != null) {
                currentPatientId = id
                onEvent(MedicalRecordEvent.LoadFullRecords)
            } else {
                _state.update { it.copy(error = "User session expired") }
            }
        }
    }

    fun onEvent(event: MedicalRecordEvent) {
        when (event) {
            MedicalRecordEvent.LoadFullRecords -> loadAllData()
            MedicalRecordEvent.Refresh -> loadAllData()
            is MedicalRecordEvent.UpdateBodyMetrics -> updateMetrics(event.weight, event.height)
            is MedicalRecordEvent.AnalysisClicked -> {
                // In a real app, you might emit a side effect here to navigate
                // For now, we just log or handle internal logic if needed
            }

            MedicalRecordEvent.AddRecordClicked -> {
                // Emit side effect for navigation
            }
        }
    }

    private fun loadAllData() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Fetching all data in parallel could be an optimization,
            // but sequential is safer for error handling in this simple scope.

            val profileResult = getPatientProfileUseCase(currentPatientId)
            val allergiesResult = getAllergiesUseCase(currentPatientId)
            val analysesResult = getAnalysesUseCase(currentPatientId)
            val vaccinationsResult = getVaccinationsUseCase(currentPatientId)
            val historyResult = getMedicalHistoryUseCase(currentPatientId)

            if (profileResult.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        patientProfile = profileResult.getOrNull(),
                        allergies = allergiesResult.getOrDefault(emptyList()),
                        analyses = analysesResult.getOrDefault(emptyList()),
                        vaccinations = vaccinationsResult.getOrDefault(emptyList()),
                        medicalHistory = historyResult.getOrDefault(emptyList())
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = profileResult.exceptionOrNull()?.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }

    private fun updateMetrics(weight: Double, height: Double) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val params = UpdatePatientMetricsUseCase.Params(currentPatientId, weight, height)
            val result = updatePatientMetricsUseCase(params)

            result.fold(
                onSuccess = { updatedProfile ->
                    _state.update { it.copy(isLoading = false, patientProfile = updatedProfile) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }
}
