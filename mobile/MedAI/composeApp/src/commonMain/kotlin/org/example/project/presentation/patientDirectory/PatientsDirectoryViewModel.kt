package org.example.project.presentation.patientDirectory

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.usecase.GetPatientsUseCase

class PatientsDirectoryViewModel(
    private val getPatientsUseCase: GetPatientsUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(PatientsDirectoryState())
    val state: StateFlow<PatientsDirectoryState> = _state.asStateFlow()

    private val _effect = Channel<PatientsDirectoryEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(PatientsDirectoryEvent.LoadPatients)
    }

    fun onEvent(event: PatientsDirectoryEvent) {
        when (event) {
            is PatientsDirectoryEvent.LoadPatients -> loadPatients()
            is PatientsDirectoryEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                filterPatients(event.query)
            }
            is PatientsDirectoryEvent.OnPatientClicked -> {
                screenModelScope.launch {
                    _effect.send(PatientsDirectoryEffect.NavigateToPatientDetails(event.patient.id))
                }
            }
            PatientsDirectoryEvent.OnAddPatientClicked -> {
                screenModelScope.launch {
                    _effect.send(PatientsDirectoryEffect.NavigateToAddPatient)
                }
            }
            PatientsDirectoryEvent.ProcessError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadPatients() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getPatientsUseCase()
                .onSuccess { patients ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            patients = patients,
                            filteredPatients = patients
                        )
                    }
                    // Re-apply filter if query exists
                    if (_state.value.searchQuery.isNotEmpty()) {
                        filterPatients(_state.value.searchQuery)
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message ?: "Unknown error") }
                    _effect.send(PatientsDirectoryEffect.ShowSnackbar(error.message ?: "Unknown error"))
                }
        }
    }

    private fun filterPatients(query: String) {
        val currentPatients = _state.value.patients
        if (query.isBlank()) {
            _state.update { it.copy(filteredPatients = currentPatients) }
        } else {
            val filtered = currentPatients.filter {
                it.fullName.contains(query, ignoreCase = true) ||
                it.id.contains(query, ignoreCase = true)
            }
            _state.update { it.copy(filteredPatients = filtered) }
        }
    }
}
