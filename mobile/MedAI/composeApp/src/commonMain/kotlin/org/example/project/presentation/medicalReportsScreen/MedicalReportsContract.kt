package org.example.project.presentation.medicalReportsScreen

import org.example.project.domain.model.MedicalReport

data class MedicalReportsState(
    val patientId: Int = 0,
    val token: String = "",
    val reports: List<MedicalReport> = emptyList(),
    val scanDataInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface MedicalReportsEvent {
    data class Init(val patientId: Int, val token: String) : MedicalReportsEvent
    data class OnScanDataChanged(val scanData: String) : MedicalReportsEvent
    object GenerateReportClicked : MedicalReportsEvent
}

sealed interface MedicalReportsEffect {
    data class ShowToast(val message: String) : MedicalReportsEffect
}
