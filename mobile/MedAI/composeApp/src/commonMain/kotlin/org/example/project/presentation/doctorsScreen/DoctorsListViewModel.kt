package org.example.project.presentation.doctorsScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.doctor.GetDoctorsUseCase

class DoctorsListViewModel(
    private val getDoctorsUseCase: GetDoctorsUseCase,
    private val specialtyId: String? = null
) : MviScreenModel<DoctorsListState, DoctorsListEvent, DoctorsListEffect>(DoctorsListState()) {

    init {
        loadDoctors()
    }

    override fun onEvent(event: DoctorsListEvent) {
        when(event) {
            is DoctorsListEvent.SearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
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
            setState { copy(isLoading = true) }
            val result = getDoctorsUseCase(specialtyId)

            result.fold(
                onSuccess = { list ->
                    setState { copy(isLoading = false, doctors = list, filteredDoctors = list) }
                },
                onFailure = { error ->
                    setState { copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun filterDoctors() {
        val query = state.value.searchQuery
        val all = state.value.doctors
        val filtered = if (query.isBlank()) all else all.filter {
            it.name.contains(query, ignoreCase = true)
        }
        setState { copy(filteredDoctors = filtered) }
    }
}
