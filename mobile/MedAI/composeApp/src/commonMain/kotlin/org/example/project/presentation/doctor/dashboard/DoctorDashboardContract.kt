package org.example.project.presentation.doctor.dashboard

import kotlinx.datetime.LocalDate
import org.example.project.domain.model.appointment.AppointmentDetail

data class DoctorDashboardState(
    val doctorName: String = "",
    val avatarUrl: String? = null,
    val uiState: DoctorDashboardUiState = DoctorDashboardUiState.Loading,
    val selectedDate: Long = 0L,
    val displayedMonth: LocalDate? = null,
    val dates: List<LocalDate> = emptyList(),
    val appointmentDates: Set<LocalDate> = emptySet(),
    val allAppointments: List<AppointmentDetail> = emptyList(),
    val filteredAppointments: List<AppointmentDetail> = emptyList()
)

sealed class DoctorDashboardUiState {
    object Loading : DoctorDashboardUiState()
    data class Success(val appointments: List<AppointmentDetail>) : DoctorDashboardUiState()
    data class Error(val message: String) : DoctorDashboardUiState()
}

sealed class DoctorDashboardEvent {
    data class OnDateSelected(val date: Long) : DoctorDashboardEvent()
    object OnPreviousMonthClicked : DoctorDashboardEvent()
    object OnNextMonthClicked : DoctorDashboardEvent()
    object Refresh : DoctorDashboardEvent()
    data class AppointmentClicked(val appointmentId: String) : DoctorDashboardEvent()
}

sealed class DoctorDashboardEffect {
    data class NavigateToAppointmentDetails(val appointmentId: String) : DoctorDashboardEffect()
    data class ShowSnackbar(val message: String) : DoctorDashboardEffect()
}
