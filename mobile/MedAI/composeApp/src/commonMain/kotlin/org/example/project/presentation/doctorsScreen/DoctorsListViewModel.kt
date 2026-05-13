package org.example.project.presentation.doctorsScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.usecase.doctor.GetDoctorsUseCase

class DoctorsListViewModel(
    private val getDoctorsUseCase: GetDoctorsUseCase,
    private val specialtyId: String? = null
) : ScreenModel {

    private val _state = MutableStateFlow(DoctorsListState())
    val state = _state.asStateFlow()

    private val _effect = Channel<DoctorsListEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadDoctors()
    }

    fun onEvent(event: DoctorsListEvent) {
        when(event) {
            is DoctorsListEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                filterDoctors()
            }
            DoctorsListEvent.BackClicked -> sendEffect(DoctorsListEffect.NavigateBack)
            is DoctorsListEvent.DoctorClicked -> sendEffect(DoctorsListEffect.NavigateToDoctorDetails(event.doctorId))
            DoctorsListEvent.SortClicked -> { /* Sort logic */ }
            DoctorsListEvent.FilterClicked -> { /* Filter logic */ }
        }
    }

    private fun loadDoctors() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = getDoctorsUseCase(specialtyId)

            result.fold(
                onSuccess = { list ->
                    _state.update { it.copy(isLoading = false, doctors = list, filteredDoctors = list) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun filterDoctors() {
        val query = _state.value.searchQuery
        val all = _state.value.doctors
        val filtered = if (query.isBlank()) all else all.filter {
            it.name.contains(query, ignoreCase = true)
        }
        _state.update { it.copy(filteredDoctors = filtered) }
    }

    private fun sendEffect(effect: DoctorsListEffect) {
        screenModelScope.launch { _effect.send(effect) }
    }
}
