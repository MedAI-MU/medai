package org.example.project.presentation.doctorsScreen

import org.example.project.domain.model.doctor.Doctor

data class DoctorsListState(
    val doctors: List<Doctor> = emptyList(),
    val filteredDoctors: List<Doctor> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class DoctorsListEvent {
    data class SearchQueryChanged(val query: String) : DoctorsListEvent()
    object BackClicked : DoctorsListEvent()
    object SortClicked : DoctorsListEvent()
    object FilterClicked : DoctorsListEvent()
    data class DoctorClicked(val doctorId: String) : DoctorsListEvent()
}

sealed class DoctorsListEffect {
    object NavigateBack : DoctorsListEffect()
    data class NavigateToDoctorDetails(val doctorId: String) : DoctorsListEffect()
    data class ShowError(val message: String) : DoctorsListEffect()
}
