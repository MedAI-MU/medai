package org.example.project.presentation.homeScreen

import kotlinx.datetime.LocalDate

sealed class HomeEvent {
    object Refresh : HomeEvent()

    // Actions
    object PreviousMonthClicked : HomeEvent()
    object NextMonthClicked : HomeEvent()
    data class DateSelected(val date: LocalDate) : HomeEvent()

    object SeeAllCategoriesClicked : HomeEvent()
    object SeeAllScheduleClicked : HomeEvent()
    object SeeAllSpecialtiesClicked : HomeEvent()

    // Item Clicks
    data class CategoryClicked(val categoryId: String) : HomeEvent()
    data class DoctorClicked(val doctorId: String) : HomeEvent()
    data class AppointmentClicked(val appointmentId: String) : HomeEvent()
    data class SpecialtyClicked(val specialtyId: String) : HomeEvent()


    // Top Bar Actions
    object NotificationsClicked : HomeEvent()
    object SettingsClicked : HomeEvent()
    object SearchClicked : HomeEvent()
}
