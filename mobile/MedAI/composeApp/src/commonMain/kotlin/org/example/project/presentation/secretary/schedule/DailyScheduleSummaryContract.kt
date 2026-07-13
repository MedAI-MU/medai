package org.example.project.presentation.secretary.schedule

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.domain.model.appointment.AppointmentDetail

data class DailyScheduleSummaryState(
    val isLoading: Boolean = false,
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val daySummaries: List<DayScheduleSummary> = emptyList(),
    val filteredAppointments: List<AppointmentDetail> = emptyList(),
    val allAppointments: List<AppointmentDetail> = emptyList(),
    val loadingAppointmentIds: Set<String> = emptySet(),
    val error: String? = null
)

data class DayScheduleSummary(
    val date: LocalDate,
    val patientCount: Int
)

sealed class DailyScheduleSummaryEvent {
    object LoadSummary : DailyScheduleSummaryEvent()
    data class SelectDate(val date: LocalDate) : DailyScheduleSummaryEvent()
    data class ApproveAppointment(val appointmentId: String) : DailyScheduleSummaryEvent()
    data class RejectAppointment(val appointmentId: String) : DailyScheduleSummaryEvent()
}

sealed class DailyScheduleSummaryEffect {
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : DailyScheduleSummaryEffect()
}
