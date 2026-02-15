package org.example.project.presentation.doctor.records

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
import org.example.project.domain.usecase.GetAllergiesUseCase
import org.example.project.domain.usecase.GetAnalysesUseCase
import org.example.project.domain.usecase.GetMedicalHistoryUseCase
import org.example.project.domain.usecase.GetPatientProfileUseCase
import org.example.project.domain.usecase.GetVaccinationsUseCase

data class DoctorPatientRecordsState(
    val isLoading: Boolean = false,
    val patientProfile: Patient? = null,
    val allergies: List<AllergyEntity> = emptyList(),
    val analyses: List<AnalysisEntity> = emptyList(),
    val vaccinations: List<VaccinationEntity> = emptyList(),
    val medicalHistory: List<MedicalHistoryEntity> = emptyList(),
    val error: String? = null
)

class DoctorPatientRecordsViewModel(
    private val patientId: String,
    private val getPatientProfileUseCase: GetPatientProfileUseCase,
    private val getAllergiesUseCase: GetAllergiesUseCase,
    private val getAnalysesUseCase: GetAnalysesUseCase,
    private val getVaccinationsUseCase: GetVaccinationsUseCase,
    private val getMedicalHistoryUseCase: GetMedicalHistoryUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(DoctorPatientRecordsState())
    val state = _state.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val profileResult = getPatientProfileUseCase(patientId)
            // In a real app, you might want to load these progressively or use async/await
            val allergiesResult = getAllergiesUseCase(patientId)
            val analysesResult = getAnalysesUseCase(patientId)
            val vaccinationsResult = getVaccinationsUseCase(patientId)
            val historyResult = getMedicalHistoryUseCase(patientId)

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
}
