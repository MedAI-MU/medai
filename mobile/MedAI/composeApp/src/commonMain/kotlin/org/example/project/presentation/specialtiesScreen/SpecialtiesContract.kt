package org.example.project.presentation.specialtiesScreen

import org.example.project.domain.model.Specialty

data class SpecialtiesState(
    val specialties: List<UiSpecialty> = emptyList(),
    val filteredSpecialties: List<UiSpecialty> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val sortOption: SortOption = SortOption.A_TO_Z
)
data class UiSpecialty(
    val origin: Specialty,
    val name: String
)

enum class SortOption(val label: String) {
    A_TO_Z("Name (A → Z)"),
    Z_TO_A("Name (Z → A)")
}

sealed class SpecialtiesEvent {
    data class SearchQueryChanged(val query: String) : SpecialtiesEvent()
    data class SpecialtyClicked(val specialtyId: String) : SpecialtiesEvent()
    object SortClicked : SpecialtiesEvent() // A-Z Sort
    object BackClicked : SpecialtiesEvent()
}

sealed class SpecialtiesEffect {
    object NavigateBack : SpecialtiesEffect()
    data class NavigateToDoctorsBySpecialty(val specialtyId: String) : SpecialtiesEffect()
    data class ShowError(val message: String) : SpecialtiesEffect()
    data class ShowMessage(val message: String) : SpecialtiesEffect()
}
