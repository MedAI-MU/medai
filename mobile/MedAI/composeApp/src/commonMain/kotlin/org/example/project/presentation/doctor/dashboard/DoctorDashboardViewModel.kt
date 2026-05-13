package org.example.project.presentation.doctor.dashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.usecase.appointment.GetDoctorAppointmentsUseCase
import org.example.project.core.presentation.util.CalendarManager

class DoctorDashboardViewModel(
    private val getDoctorAppointmentsUseCase: GetDoctorAppointmentsUseCase,
    calendarManager: CalendarManager
) : ScreenModel {

    private val _uiState = MutableStateFlow<DoctorDashboardUiState>(DoctorDashboardUiState.Loading)
    val uiState: StateFlow<DoctorDashboardUiState> = _uiState

    private val _selectedDate = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
    val selectedDate: StateFlow<Long> = _selectedDate

    // Expose a list of days for the current week/month for the DayPicker
    val dates = calendarManager.getDaysForMonth(calendarManager.getToday())

    init {
        fetchAppointments()
    }

    fun onDateSelected(date: Long) {
        _selectedDate.value = date
        fetchAppointments()
    }

    fun refresh() {
        fetchAppointments()
    }

    private fun fetchAppointments() {
        screenModelScope.launch {
            _uiState.value = DoctorDashboardUiState.Loading
            getDoctorAppointmentsUseCase()
                .onSuccess { appointments ->
                    _uiState.value = DoctorDashboardUiState.Success(appointments)
                }
                .onFailure {
                    _uiState.value = DoctorDashboardUiState.Error("Failed to load appointments")
                }
        }
    }
}

sealed class DoctorDashboardUiState {
    object Loading : DoctorDashboardUiState()
    data class Success(val appointments: List<AppointmentDetail>) : DoctorDashboardUiState()
    data class Error(val message: String) : DoctorDashboardUiState()
}
