package org.example.project.presentation.doctor.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.appointment.GetDoctorAppointmentsUseCase
import org.example.project.domain.usecase.profile.GetProfileUseCase
import org.example.project.core.presentation.util.CalendarManager

class DoctorDashboardViewModel(
    private val getDoctorAppointmentsUseCase: GetDoctorAppointmentsUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val calendarManager: CalendarManager
) : MviScreenModel<DoctorDashboardState, DoctorDashboardEvent, DoctorDashboardEffect>(
    initialState = DoctorDashboardState(
        selectedDate = Clock.System.now().toEpochMilliseconds(),
        displayedMonth = calendarManager.getToday(),
        dates = calendarManager.getDaysForMonth(calendarManager.getToday())
    )
) {

    init {
        fetchAppointments()
        loadDoctorProfile()
    }

    private fun loadDoctorProfile() {
        screenModelScope.launch {
            getProfileUseCase().fold(
                onSuccess = { user ->
                    setState { copy(doctorName = user.name, avatarUrl = user.avatarUrl) }
                },
                onFailure = {}
            )
        }
    }

    override fun onEvent(event: DoctorDashboardEvent) {
        when (event) {
            is DoctorDashboardEvent.OnDateSelected -> {
                setState { copy(selectedDate = event.date) }
                filterAppointmentsBySelectedDate()
            }
            DoctorDashboardEvent.OnPreviousMonthClicked -> {
                val current = state.value.displayedMonth ?: calendarManager.getToday()
                changeDisplayedMonth(current.minus(DatePeriod(months = 1)))
            }
            DoctorDashboardEvent.OnNextMonthClicked -> {
                val current = state.value.displayedMonth ?: calendarManager.getToday()
                changeDisplayedMonth(current.plus(DatePeriod(months = 1)))
            }
            DoctorDashboardEvent.Refresh -> fetchAppointments()
            is DoctorDashboardEvent.AppointmentClicked -> {
                sendEffect(DoctorDashboardEffect.NavigateToAppointmentDetails(event.appointmentId))
            }
        }
    }

    private fun changeDisplayedMonth(newMonthBase: LocalDate) {
        val newDates = calendarManager.getDaysForMonth(newMonthBase)
        setState { copy(displayedMonth = newMonthBase, dates = newDates) }

        val firstDay = newDates.firstOrNull()
        if (firstDay != null) {
            val epoch = firstDay.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            setState { copy(selectedDate = epoch) }
            filterAppointmentsBySelectedDate()
        }
    }

    private fun fetchAppointments() {
        screenModelScope.launch {
            setState { copy(uiState = DoctorDashboardUiState.Loading) }
            getDoctorAppointmentsUseCase()
                .onSuccess { appointments ->
                    val apptDates = appointments.map { it.date.date }.toSet()
                    setState {
                        copy(
                            allAppointments = appointments,
                            appointmentDates = apptDates,
                            uiState = DoctorDashboardUiState.Success(appointments)
                        )
                    }
                    filterAppointmentsBySelectedDate()
                }
                .onFailure { error ->
                    setState { copy(uiState = DoctorDashboardUiState.Error(error.message ?: "Failed to fetch appointments")) }
                    sendEffect(DoctorDashboardEffect.ShowSnackbar(error.message ?: "Failed to fetch appointments"))
                }
        }
    }

    private fun filterAppointmentsBySelectedDate() {
        val currentState = state.value
        val selectedLocalDate = kotlinx.datetime.Instant.fromEpochMilliseconds(currentState.selectedDate)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date

        val filtered = currentState.allAppointments.filter { it.date.date == selectedLocalDate }
        setState { copy(filteredAppointments = filtered) }
    }
}
