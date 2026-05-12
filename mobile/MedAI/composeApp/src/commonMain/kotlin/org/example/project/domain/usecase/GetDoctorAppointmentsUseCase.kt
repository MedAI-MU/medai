package org.example.project.domain.usecase

import org.example.project.domain.model.AppointmentDetail
import org.example.project.domain.repository.AppointmentRepository

class GetDoctorAppointmentsUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(): Result<List<AppointmentDetail>> {
        return repository.getMyAppointments()
    }
}
