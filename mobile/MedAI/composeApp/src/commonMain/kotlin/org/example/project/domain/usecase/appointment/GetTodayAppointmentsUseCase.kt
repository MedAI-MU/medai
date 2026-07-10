package org.example.project.domain.usecase.appointment

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.repository.appointment.AppointmentRepository

class GetTodayAppointmentsUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(): Result<List<AppointmentDetail>> {
        return repository.getAllAppointments().map { list ->
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            list.filter { it.date.date == today }
        }
    }
}
