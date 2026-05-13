package org.example.project.presentation.doctor.dashboard

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.usecase.appointment.GetDoctorAppointmentsUseCase
import org.example.project.core.presentation.util.CalendarManager

class DoctorDashboardViewModel(
    private val getDoctorAppointmentsUseCase: GetDoctorAppointmentsUseCase,
    private val calendarManager: CalendarManager
) : ScreenModel {

    private val _uiState = MutableStateFlow<DoctorDashboardUiState>(DoctorDashboardUiState.Loading)
    val uiState: StateFlow<DoctorDashboardUiState> = _uiState

    private val _selectedDate = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
    val selectedDate: StateFlow<Long> = _selectedDate

    /** The month currently being displayed in the date strip. */
    private val _displayedMonth = MutableStateFlow(calendarManager.getToday())
    val displayedMonth: StateFlow<LocalDate> = _displayedMonth

    /** The list of day cells for the currently displayed month. */
    private val _dates = MutableStateFlow(calendarManager.getDaysForMonth(calendarManager.getToday()))
    val dates: StateFlow<List<LocalDate>> = _dates

    /** Set of LocalDate values that have at least one appointment — drives the dot indicator on date cards. */
    private val _appointmentDates = MutableStateFlow<Set<LocalDate>>(emptySet())
    val appointmentDates: StateFlow<Set<LocalDate>> = _appointmentDates

    /** All appointments (unfiltered) — kept to derive per-date filtering. */
    private val _allAppointments = MutableStateFlow<List<AppointmentDetail>>(emptyList())

    /** Appointments filtered to the currently selected date. */
    private val _filteredAppointments = MutableStateFlow<List<AppointmentDetail>>(emptyList())
    val filteredAppointments: StateFlow<List<AppointmentDetail>> = _filteredAppointments

    init {
        fetchAppointments()
    }

    fun onDateSelected(date: Long) {
        _selectedDate.value = date
        filterAppointmentsBySelectedDate()
    }

    fun onPreviousMonthClicked() {
        val current = _displayedMonth.value
        val newMonth = current.minus(DatePeriod(months = 1))
        changeDisplayedMonth(newMonth)
    }

    fun onNextMonthClicked() {
        val current = _displayedMonth.value
        val newMonth = current.plus(DatePeriod(months = 1))
        changeDisplayedMonth(newMonth)
    }

    private fun changeDisplayedMonth(newMonthBase: LocalDate) {
        _displayedMonth.value = newMonthBase
        _dates.value = calendarManager.getDaysForMonth(newMonthBase)
        // Auto-select the first day of the new month
        val firstDay = _dates.value.firstOrNull()
        if (firstDay != null) {
            val epoch = firstDay.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            _selectedDate.value = epoch
            filterAppointmentsBySelectedDate()
        }
    }

    fun refresh() {
        fetchAppointments()
    }

    private fun fetchAppointments() {
        screenModelScope.launch {
            _uiState.value = DoctorDashboardUiState.Loading
            getDoctorAppointmentsUseCase()
                .onSuccess { appointments ->
                    _allAppointments.value = appointments

                    // Build the set of dates that have appointments for the dot indicators
                    _appointmentDates.value = appointments
                        .map { it.date.date } // LocalDateTime -> LocalDate
                        .toSet()

                    _uiState.value = DoctorDashboardUiState.Success(appointments)
                    filterAppointmentsBySelectedDate()
                }
                .onFailure {
                    _uiState.value = DoctorDashboardUiState.Error("Failed to load appointments")
                }
        }
    }

    /**
     * Filters _allAppointments by the currently selected epoch date and updates _filteredAppointments.
     */
    private fun filterAppointmentsBySelectedDate() {
        val selectedEpoch = _selectedDate.value
        val selectedLocalDate = kotlinx.datetime.Instant.fromEpochMilliseconds(selectedEpoch)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        _filteredAppointments.value = _allAppointments.value.filter { appointment ->
            appointment.date.date == selectedLocalDate
        }
    }
}

sealed class DoctorDashboardUiState {
    object Loading : DoctorDashboardUiState()
    data class Success(val appointments: List<AppointmentDetail>) : DoctorDashboardUiState()
    data class Error(val message: String) : DoctorDashboardUiState()
}
