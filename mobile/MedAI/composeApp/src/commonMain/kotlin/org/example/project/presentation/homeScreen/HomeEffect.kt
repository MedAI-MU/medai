package org.example.project.presentation.homeScreen

sealed class HomeEffect {
    // Navigation Effects
    data class NavigateToCategory(val categoryId: String) : HomeEffect()
    data class NavigateToDoctorDetails(val doctorId: String) : HomeEffect()
    data class NavigateToAppointmentDetails(val appointmentId: String) : HomeEffect()
    data class NavigateToSpecialty(val specialtyId: String) : HomeEffect()

    // See All Navigation
    object NavigateToAllCategories : HomeEffect()
    object NavigateToFullSchedule : HomeEffect()
    object NavigateToAllSpecialties : HomeEffect()

    // Top Bar Navigation
    object NavigateToNotifications : HomeEffect()
    object NavigateToSettings : HomeEffect()
    object NavigateToSearch : HomeEffect()

    // Other
    data class ShowError(val message: String) : HomeEffect()
    data class ShowMessage(val message: String) : HomeEffect()
}
