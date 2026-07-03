package org.example.project.presentation.secretary.doctors

import org.example.project.domain.model.doctor.Doctor
import org.example.project.domain.model.doctor.Speciality

data class SecretaryDoctorListState(
    val doctors: List<Doctor> = emptyList(),
    val filteredDoctors: List<Doctor> = emptyList(),
    val allSpecialities: List<Speciality> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isActionLoading: Boolean = false,
    val selectedDoctorForSpeciality: Doctor? = null,
    val showSpecialityDialog: Boolean = false,
    val showCreateSpecialityDialog: Boolean = false
)

sealed class SecretaryDoctorListEvent {
    object LoadDoctors : SecretaryDoctorListEvent()
    data class OnSearchQueryChanged(val query: String) : SecretaryDoctorListEvent()
    data class ShowSpecialityDialog(val doctor: Doctor) : SecretaryDoctorListEvent()
    object DismissSpecialityDialog : SecretaryDoctorListEvent()
    data class AssignSpeciality(
        val doctorId: String,
        val specialityId: Int,
        val isPrimary: Boolean,
        val yearsOfExperience: Int
    ) : SecretaryDoctorListEvent()
    data class RemoveSpeciality(
        val doctorId: String,
        val doctorSpecialityId: Int
    ) : SecretaryDoctorListEvent()
    object ShowCreateSpecialityDialog : SecretaryDoctorListEvent()
    object DismissCreateSpecialityDialog : SecretaryDoctorListEvent()
    data class CreateSpeciality(val name: String) : SecretaryDoctorListEvent()
}

sealed class SecretaryDoctorListEffect {
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : SecretaryDoctorListEffect()
}
