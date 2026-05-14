package org.example.project.domain.usecase.appointment

import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.repository.appointment.AppointmentRepository

class GetDoctorAppointmentsUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(): Result<List<AppointmentDetail>> {
        return repository.getMyAppointments()
    }
}
