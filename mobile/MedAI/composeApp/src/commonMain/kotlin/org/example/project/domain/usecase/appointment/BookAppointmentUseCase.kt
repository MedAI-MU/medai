package org.example.project.domain.usecase.appointment

import org.example.project.domain.repository.appointment.AppointmentRepository
import org.example.project.domain.repository.doctor.DoctorRepository

class BookAppointmentUseCase(
    private val repository: DoctorRepository,
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(
        doctorId: String,
        slotId: String
    ): Result<String> {
        // Business Logic Validation
        if (slotId.isBlank()) return Result.failure(Exception("Time slot is required"))

        val result = repository.bookAppointment(doctorId, slotId)
        if (result.isSuccess) {
            appointmentRepository.triggerAppointmentsRefresh()
        }
        return result
    }
}
