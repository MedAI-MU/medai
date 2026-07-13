package org.example.project.presentation.secretary.schedule

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.repository.appointment.AppointmentRepository
import org.example.project.domain.usecase.secretary.CheckInPatientUseCase
import org.example.project.domain.usecase.appointment.CancelAppointmentUseCase

class DailyScheduleSummaryViewModel(
    private val appointmentRepository: AppointmentRepository,
    private val checkInPatientUseCase: CheckInPatientUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase
) : MviScreenModel<DailyScheduleSummaryState, DailyScheduleSummaryEvent, DailyScheduleSummaryEffect>(DailyScheduleSummaryState()) {

    init {
        onEvent(DailyScheduleSummaryEvent.LoadSummary)
    }

    override fun onEvent(event: DailyScheduleSummaryEvent) {
        when (event) {
            is DailyScheduleSummaryEvent.LoadSummary -> loadSummaryData()
            is DailyScheduleSummaryEvent.SelectDate -> selectDate(event.date)
            is DailyScheduleSummaryEvent.ApproveAppointment -> approveAppointment(event.appointmentId)
            is DailyScheduleSummaryEvent.RejectAppointment -> rejectAppointment(event.appointmentId)
        }
    }

    private fun loadSummaryData() {
        setState { copy(isLoading = true) }
        screenModelScope.launch {
            appointmentRepository.getAllAppointments().fold(
                onSuccess = { appointments ->
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                    // Generate upcoming 7 days starting today
                    val upcomingDays = (0..6).map { offset ->
                        today.plus(offset, DateTimeUnit.DAY)
                    }

                    // Count scheduled patients for each day
                    val summaries = upcomingDays.map { date ->
                        val count = appointments.count { it.date.date == date }
                        DayScheduleSummary(date, count)
                    }

                    val filteredAppts = appointments.filter { it.date.date == state.value.selectedDate }

                    setState {
                        copy(
                            isLoading = false,
                            daySummaries = summaries,
                            filteredAppointments = filteredAppts,
                            allAppointments = appointments,
                            error = null
                        )
                    }
                },
                onFailure = { throwable ->
                    setState { copy(isLoading = false, error = throwable.message) }
                    sendEffect(DailyScheduleSummaryEffect.ShowSnackbar(throwable.message ?: "Failed to load schedule summaries", isError = true))
                }
            )
        }
    }

    private fun selectDate(date: LocalDate) {
        val filtered = state.value.allAppointments.filter { it.date.date == date }
        setState {
            copy(
                selectedDate = date,
                filteredAppointments = filtered
            )
        }
    }

    private fun approveAppointment(appointmentId: String) {
        setState { copy(loadingAppointmentIds = loadingAppointmentIds + appointmentId) }
        screenModelScope.launch {
            checkInPatientUseCase(appointmentId).fold(
                onSuccess = {
                    setState { copy(loadingAppointmentIds = loadingAppointmentIds - appointmentId) }
                    sendEffect(DailyScheduleSummaryEffect.ShowSnackbar("Appointment approved successfully"))
                    loadSummaryData()
                },
                onFailure = { throwable ->
                    setState { copy(loadingAppointmentIds = loadingAppointmentIds - appointmentId) }
                    sendEffect(DailyScheduleSummaryEffect.ShowSnackbar(throwable.message ?: "Failed to approve appointment", isError = true))
                }
            )
        }
    }

    private fun rejectAppointment(appointmentId: String) {
        setState { copy(loadingAppointmentIds = loadingAppointmentIds + appointmentId) }
        screenModelScope.launch {
            cancelAppointmentUseCase(appointmentId).fold(
                onSuccess = {
                    setState { copy(loadingAppointmentIds = loadingAppointmentIds - appointmentId) }
                    sendEffect(DailyScheduleSummaryEffect.ShowSnackbar("Appointment rejected successfully"))
                    loadSummaryData()
                },
                onFailure = { throwable ->
                    setState { copy(loadingAppointmentIds = loadingAppointmentIds - appointmentId) }
                    sendEffect(DailyScheduleSummaryEffect.ShowSnackbar(throwable.message ?: "Failed to reject appointment", isError = true))
                }
            )
        }
    }
}
