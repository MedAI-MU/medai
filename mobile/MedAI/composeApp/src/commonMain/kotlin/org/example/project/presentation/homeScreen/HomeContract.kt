package org.example.project.presentation.homeScreen

import kotlinx.datetime.LocalDate
import org.example.project.domain.model.appointment.Appointment
import org.example.project.domain.model.home.Category
import org.example.project.domain.model.specialty.Specialty

data class CalendarUiModel(
    val day: String,      // e.g. "9"
    val weekDay: String,  // e.g. "MON"
    val fullDate: LocalDate, // e.g. "2025-11-09"
    val isSelected: Boolean = false,
    val hasAppointment: Boolean = false,
    val isEnabled: Boolean = true
)

data class HomeState(
    val userName: String = "",
    val avatarUrl: String? = null,
    val categories: List<Category> = emptyList(),

    // Data List
    val allAppointments: List<Appointment> = emptyList(),
    val filteredAppointments: List<Appointment> = emptyList(),
    val specialties: List<Specialty> = emptyList(),

    // Calender State
    val calendarDays: List<CalendarUiModel> = emptyList(),
    val selectedDate: LocalDate? = null,
    val displayedMonth: LocalDate? = null,

    val isLoading: Boolean = true,
    val error: String? = null
)

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
    data class CategoryClicked(val category: Category) : HomeEvent()
    data class DoctorClicked(val doctorId: String) : HomeEvent()
    data class AppointmentClicked(val appointmentId: String) : HomeEvent()
    data class SpecialtyClicked(val specialtyId: String) : HomeEvent()


    // Top Bar Actions
    object NotificationsClicked : HomeEvent()
    object SettingsClicked : HomeEvent()
    object SearchClicked : HomeEvent()
}

sealed class HomeEffect {
    // Navigation Effects
    data class NavigateToCategory(val category: Category) : HomeEffect()
    data class NavigateToDoctorDetails(val doctorId: String) : HomeEffect()
    data class NavigateToAppointmentDetails(val appointmentId: String) : HomeEffect()
    data class NavigateToSpecialty(val specialtyId: String,val title: String) : HomeEffect()

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
