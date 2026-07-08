package org.example.project.presentation.specialtiesScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.core.domain.ResourceProvider
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.specialty.GetSpecialtiesUseCase

class SpecialtiesViewModel(
    private val getSpecialtiesUseCase: GetSpecialtiesUseCase,
    private val resourceProvider: ResourceProvider
) : MviScreenModel<SpecialtiesState, SpecialtiesEvent, SpecialtiesEffect>(SpecialtiesState()) {

    private var searchJob: Job? = null

    init {
        loadSpecialties()
    }

    override fun onEvent(event: SpecialtiesEvent) {
        when(event) {
            is SpecialtiesEvent.SearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
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
                val newOption = if (state.value.sortOption == SortOption.A_TO_Z) {
                    SortOption.Z_TO_A
                } else {
                    SortOption.A_TO_Z
                }

                setState { copy(sortOption = newOption) }
                calculateDisplayedList()
            }
        }
    }

    private fun loadSpecialties() {
        screenModelScope.launch {
            setState { copy(isLoading = true) }
            val result = getSpecialtiesUseCase()

            result.fold(
                onSuccess = { list ->
                    val uiSpecialties = list.map { specialty ->
                        UiSpecialty(
                            origin = specialty,
                            name = resourceProvider.getString(specialty.title)
                        )
                    }

                    setState {
                        copy(
                            isLoading = false,
                            specialties = uiSpecialties
                        )
                    }
                    calculateDisplayedList()
                },
                onFailure = { error ->
                    setState { copy(isLoading = false, error = error.message) }
                    sendEffect(SpecialtiesEffect.ShowError(error.message ?: "Unknown Error"))
                }
            )
        }
    }

    private fun calculateDisplayedList() {
        val currentState = state.value
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
        setState { copy(filteredSpecialties = result) }
    }
}
