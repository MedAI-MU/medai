package org.example.project.presentation.secretary.doctors

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.model.doctor.Doctor
import org.example.project.domain.usecase.doctor.GetDoctorsUseCase
import org.example.project.domain.usecase.doctor.GetAllSpecialitiesUseCase
import org.example.project.domain.usecase.doctor.AssignSpecialityUseCase
import org.example.project.domain.usecase.doctor.RemoveSpecialityUseCase
import org.example.project.domain.usecase.doctor.CreateSpecialityUseCase

class SecretaryDoctorListViewModel(
    private val getDoctorsUseCase: GetDoctorsUseCase,
    private val getAllSpecialitiesUseCase: GetAllSpecialitiesUseCase,
    private val assignSpecialityUseCase: AssignSpecialityUseCase,
    private val removeSpecialityUseCase: RemoveSpecialityUseCase,
    private val createSpecialityUseCase: CreateSpecialityUseCase
) : MviScreenModel<SecretaryDoctorListState, SecretaryDoctorListEvent, SecretaryDoctorListEffect>(
    SecretaryDoctorListState()
) {

    init {
        loadDoctors()
        loadSpecialities()
    }

    override fun onEvent(event: SecretaryDoctorListEvent) {
        when (event) {
            is SecretaryDoctorListEvent.LoadDoctors -> loadDoctors()
            is SecretaryDoctorListEvent.OnSearchQueryChanged -> {
                setState { copy(
                    searchQuery = event.query,
                    filteredDoctors = filterList(doctors, event.query)
                )}
            }
            is SecretaryDoctorListEvent.ShowSpecialityDialog -> {
                setState { copy(selectedDoctorForSpeciality = event.doctor, showSpecialityDialog = true) }
            }
            is SecretaryDoctorListEvent.DismissSpecialityDialog -> {
                setState { copy(selectedDoctorForSpeciality = null, showSpecialityDialog = false) }
            }
            is SecretaryDoctorListEvent.AssignSpeciality -> {
                assignSpeciality(event.doctorId, event.specialityId, event.isPrimary, event.yearsOfExperience)
            }
            is SecretaryDoctorListEvent.RemoveSpeciality -> {
                removeSpeciality(event.doctorId, event.doctorSpecialityId)
            }
            SecretaryDoctorListEvent.ShowCreateSpecialityDialog -> {
                setState { copy(showCreateSpecialityDialog = true) }
            }
            SecretaryDoctorListEvent.DismissCreateSpecialityDialog -> {
                setState { copy(showCreateSpecialityDialog = false) }
            }
            is SecretaryDoctorListEvent.CreateSpeciality -> {
                createSpeciality(event.name)
            }
        }
    }

    private fun loadDoctors() {
        screenModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getDoctorsUseCase()
                .onSuccess { doctors ->
                    setState { copy(
                        isLoading = false,
                        doctors = doctors,
                        filteredDoctors = filterList(doctors, searchQuery)
                    )}
                }
                .onFailure { e ->
                    setState { copy(isLoading = false, error = e.message ?: "Failed to load doctors") }
                }
        }
    }

    private fun loadSpecialities() {
        screenModelScope.launch {
            getAllSpecialitiesUseCase()
                .onSuccess { specialities ->
                    setState { copy(allSpecialities = specialities) }
                }
                .onFailure { /* silently fail, dialog will show empty list */ }
        }
    }

    private fun filterList(doctors: List<Doctor>, query: String): List<Doctor> {
        if (query.isBlank()) return doctors
        return doctors.filter {
            it.name.contains(query, ignoreCase = true) || it.specialty.contains(query, ignoreCase = true)
        }
    }

    private fun assignSpeciality(doctorId: String, specialityId: Int, isPrimary: Boolean, yearsOfExperience: Int) {
        screenModelScope.launch {
            setState { copy(isActionLoading = true) }
            assignSpecialityUseCase(doctorId, specialityId, isPrimary, yearsOfExperience)
                .onSuccess {
                    setState { copy(isActionLoading = false) }
                    sendEffect(SecretaryDoctorListEffect.ShowSnackbar("Speciality assigned successfully"))
                    loadDoctors()
                }
                .onFailure { e ->
                    setState { copy(isActionLoading = false) }
                    sendEffect(SecretaryDoctorListEffect.ShowSnackbar("Error: ${e.message}", isError = true))
                }
        }
    }

    private fun removeSpeciality(doctorId: String, doctorSpecialityId: Int) {
        screenModelScope.launch {
            setState { copy(isActionLoading = true) }
            removeSpecialityUseCase(doctorId, doctorSpecialityId)
                .onSuccess {
                    setState { copy(isActionLoading = false) }
                    sendEffect(SecretaryDoctorListEffect.ShowSnackbar("Speciality removed"))
                    loadDoctors()
                }
                .onFailure { e ->
                    setState { copy(isActionLoading = false) }
                    sendEffect(SecretaryDoctorListEffect.ShowSnackbar("Error: ${e.message}", isError = true))
                }
        }
    }

    private fun createSpeciality(name: String) {
        screenModelScope.launch {
            setState { copy(isActionLoading = true) }
            createSpecialityUseCase(name)
                .onSuccess { newSpec ->
                    setState { copy(
                        isActionLoading = false,
                        showCreateSpecialityDialog = false
                    ) }
                    sendEffect(SecretaryDoctorListEffect.ShowSnackbar("Speciality '$name' created successfully"))
                    loadSpecialities()
                }
                .onFailure { e ->
                    setState { copy(isActionLoading = false) }
                    sendEffect(SecretaryDoctorListEffect.ShowSnackbar("Error: ${e.message}", isError = true))
                }
        }
    }
}
