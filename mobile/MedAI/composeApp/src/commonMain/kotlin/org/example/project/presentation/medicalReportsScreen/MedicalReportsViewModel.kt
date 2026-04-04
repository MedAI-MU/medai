package org.example.project.presentation.medicalReportsScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.model.CreateMedicalReportRequest
import org.example.project.domain.usecase.CreateMedicalReportUseCase
import org.example.project.domain.usecase.GetMedicalReportsUseCase

class MedicalReportsViewModel(
    private val getMedicalReportsUseCase: GetMedicalReportsUseCase,
    private val createMedicalReportUseCase: CreateMedicalReportUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(MedicalReportsState())
    val state: StateFlow<MedicalReportsState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MedicalReportsEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: MedicalReportsEvent) {
        when (event) {
            is MedicalReportsEvent.Init -> {
                _state.update { it.copy(patientId = event.patientId, token = event.token) }
                loadReports()
            }
            is MedicalReportsEvent.OnScanDataChanged -> {
                _state.update { it.copy(scanDataInput = event.scanData) }
            }
            MedicalReportsEvent.GenerateReportClicked -> {
                generateReport()
            }
        }
    }

    private fun loadReports() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getMedicalReportsUseCase(_state.value.patientId, _state.value.token)
            result.fold(
                onSuccess = { reports ->
                    _state.update { it.copy(reports = reports, isLoading = false) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = "Failed to load reports: ${error.message}") }
                    _effect.emit(MedicalReportsEffect.ShowToast("Error loading reports"))
                }
            )
        }
    }

    private fun generateReport() {
        val currentScanData = _state.value.scanDataInput
        if (currentScanData.isBlank()) return

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val request = CreateMedicalReportRequest(scanData = currentScanData)
            val result = createMedicalReportUseCase(_state.value.patientId, _state.value.token, request)

            result.fold(
                onSuccess = { newReport ->
                    _state.update {
                        it.copy(
                            reports = listOf(newReport) + it.reports,
                            scanDataInput = "",
                            isLoading = false
                        )
                    }
                    _effect.emit(MedicalReportsEffect.ShowToast("Report generated successfully"))
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = "Failed to generate report: ${error.message}") }
                    _effect.emit(MedicalReportsEffect.ShowToast("Error generating report"))
                }
            )
        }
    }
}
