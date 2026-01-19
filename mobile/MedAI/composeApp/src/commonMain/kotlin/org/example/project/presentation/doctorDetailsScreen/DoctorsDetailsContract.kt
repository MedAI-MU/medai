package org.example.project.presentation.doctorDetailsScreen

import org.example.project.domain.model.Doctor

data class DoctorDetailsState(
    val doctor: Doctor? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class DoctorDetailsEvent {
    object BackClicked : DoctorDetailsEvent()
    object BookClicked : DoctorDetailsEvent()
    object MessageClicked : DoctorDetailsEvent()
}

sealed class DoctorDetailsEffect {
    object NavigateBack : DoctorDetailsEffect()
    object NavigateToBooking : DoctorDetailsEffect()
    data class ShowError(val message: String) : DoctorDetailsEffect()

    data class NavigateToChat(val doctorId: String, val doctorName: String) : DoctorDetailsEffect()
}
