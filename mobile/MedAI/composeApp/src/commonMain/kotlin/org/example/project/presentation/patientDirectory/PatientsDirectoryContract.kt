package org.example.project.presentation.patientDirectory

import org.example.project.domain.model.patient.Patient

data class PatientsDirectoryState(
    val isLoading: Boolean = false,
    val patients: List<Patient> = emptyList(),
    val filteredPatients: List<Patient> = emptyList(),
    val error: String? = null,
    val searchQuery: String = ""
)

sealed interface PatientsDirectoryEvent {
    data object LoadPatients : PatientsDirectoryEvent
    data class OnSearchQueryChanged(val query: String) : PatientsDirectoryEvent
    data class OnPatientClicked(val patient: Patient) : PatientsDirectoryEvent
    data object OnAddPatientClicked : PatientsDirectoryEvent
    data object ProcessError : PatientsDirectoryEvent
}

sealed interface PatientsDirectoryEffect {
    data class NavigateToPatientDetails(val patientId: String) : PatientsDirectoryEffect
    data object NavigateToAddPatient : PatientsDirectoryEffect
    data class ShowSnackbar(val message: String) : PatientsDirectoryEffect
}
