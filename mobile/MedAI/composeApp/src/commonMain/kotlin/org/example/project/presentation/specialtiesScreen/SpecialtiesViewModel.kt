package org.example.project.presentation.specialtiesScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.domain.ResourceProvider
import org.example.project.domain.usecase.specialty.GetSpecialtiesUseCase

class SpecialtiesViewModel(
    private val getSpecialtiesUseCase: GetSpecialtiesUseCase,
    private val resourceProvider: ResourceProvider
) : ScreenModel {

    private val _state = MutableStateFlow(SpecialtiesState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SpecialtiesEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        loadSpecialties()
    }

    fun onEvent(event: SpecialtiesEvent) {
        when(event) {
            is SpecialtiesEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                searchJob?.cancel()
                searchJob = screenModelScope.launch {
                    delay(300)
                    calculateDisplayedList()
                }
            }
            is SpecialtiesEvent.SpecialtyClicked -> {
                sendEffect(SpecialtiesEffect.NavigateToDoctorsBySpecialty(event.specialtyId))
            }
            SpecialtiesEvent.BackClicked -> {
                sendEffect(SpecialtiesEffect.NavigateBack)
            }
            // Toolbar Buttons
            SpecialtiesEvent.SortClicked -> {
                val newOption = if (_state.value.sortOption == SortOption.A_TO_Z) {
                    SortOption.Z_TO_A
                } else {
                    SortOption.A_TO_Z
                }

                _state.update { it.copy(sortOption = newOption) }
                calculateDisplayedList()
            }
        }
    }

    private fun loadSpecialties() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = getSpecialtiesUseCase()

            result.fold(
                onSuccess = { list ->
                    val uiSpecialties = list.map { specialty ->
                        UiSpecialty(
                            origin = specialty,
                            name = resourceProvider.getString(specialty.title)
                        )
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            specialties = uiSpecialties
                        )
                    }
                    calculateDisplayedList()
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    sendEffect(SpecialtiesEffect.ShowError(error.message ?: "Unknown Error"))
                }
            )
        }
    }

    private fun calculateDisplayedList() {
        val currentState = _state.value
        val query = currentState.searchQuery.trim()

        // 1. Start with all items
        var result = currentState.specialties

        // 2. Apply Search
        if (query.isNotEmpty()) {
            result = result.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        result = when (currentState.sortOption) {
            SortOption.A_TO_Z -> result.sortedBy { it.name }
            SortOption.Z_TO_A -> result.sortedByDescending { it.name }
        }
        // 5. Update UI
        _state.update { it.copy(filteredSpecialties = result) }
    }

    private fun sendEffect(effect: SpecialtiesEffect) {
        screenModelScope.launch { _effect.send(effect) }
    }
}
