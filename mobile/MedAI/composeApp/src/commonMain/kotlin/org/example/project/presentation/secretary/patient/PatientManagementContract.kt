package org.example.project.presentation.secretary.patient

import org.example.project.domain.model.patient.Patient

data class PatientManagementState(
    val patients: List<Patient> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class PatientManagementEvent {
    object LoadPatients : PatientManagementEvent()
}

sealed class PatientManagementEffect {
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : PatientManagementEffect()
}
