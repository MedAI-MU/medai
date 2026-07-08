package org.example.project.presentation.patientDirectory

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.patient.GetPatientsUseCase

class PatientsDirectoryViewModel(
    private val getPatientsUseCase: GetPatientsUseCase
) : MviScreenModel<PatientsDirectoryState, PatientsDirectoryEvent, PatientsDirectoryEffect>(PatientsDirectoryState()) {

    init {
        onEvent(PatientsDirectoryEvent.LoadPatients)
    }

    override fun onEvent(event: PatientsDirectoryEvent) {
        when (event) {
            is PatientsDirectoryEvent.LoadPatients -> loadPatients()
            is PatientsDirectoryEvent.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                filterPatients(event.query)
            }
            is PatientsDirectoryEvent.OnPatientClicked -> {
                screenModelScope.launch { sendEffect(PatientsDirectoryEffect.NavigateToPatientDetails(event.patient.id)) }
            }
            PatientsDirectoryEvent.OnAddPatientClicked -> {
                screenModelScope.launch { sendEffect(PatientsDirectoryEffect.NavigateToAddPatient) }
            }
            PatientsDirectoryEvent.ProcessError -> {
                setState { copy(error = null) }
            }
        }
    }

    private fun loadPatients() {
        screenModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getPatientsUseCase()
                .onSuccess { patients ->
                    setState {
                        copy(
                            isLoading = false,
                            patients = patients,
                            filteredPatients = patients
                        )
                    }
                    // Re-apply filter if query exists
                    if (state.value.searchQuery.isNotEmpty()) {
                        filterPatients(state.value.searchQuery)
                    }
                }
                .onFailure { error ->
                    setState { copy(isLoading = false, error = error.message ?: "Unknown error") }
                    sendEffect(PatientsDirectoryEffect.ShowSnackbar(error.message ?: "Unknown error"))
                }
        }
    }

    private fun filterPatients(query: String) {
        val currentPatients = state.value.patients
        if (query.isBlank()) {
            setState { copy(filteredPatients = currentPatients) }
        } else {
            val filtered = currentPatients.filter {
                it.fullName.contains(query, ignoreCase = true) ||
                it.id.contains(query, ignoreCase = true)
            }
            setState { copy(filteredPatients = filtered) }
        }
    }
}
