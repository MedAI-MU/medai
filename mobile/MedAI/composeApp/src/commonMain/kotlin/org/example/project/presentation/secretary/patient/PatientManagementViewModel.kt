package org.example.project.presentation.secretary.patient

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.secretary.CreatePatientUseCase
import org.example.project.domain.usecase.secretary.GetAllPatientsUseCase

class PatientManagementViewModel(
    private val getAllPatientsUseCase: GetAllPatientsUseCase,
    private val createPatientUseCase: CreatePatientUseCase // Kept in constructor in case we add CreatePatient event later
) : MviScreenModel<PatientManagementState, PatientManagementEvent, PatientManagementEffect>(PatientManagementState()) {

    init {
        onEvent(PatientManagementEvent.LoadPatients)
    }

    override fun onEvent(event: PatientManagementEvent) {
        when (event) {
            is PatientManagementEvent.LoadPatients -> loadPatients()
        }
    }

    private fun loadPatients() {
        screenModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val patients = getAllPatientsUseCase()
                setState { copy(patients = patients, isLoading = false) }
            } catch (e: Exception) {
                setState { copy(error = e.message, isLoading = false) }
                sendEffect(PatientManagementEffect.ShowSnackbar(e.message ?: "Failed to load patients", isError = true))
            }
        }
    }
}
