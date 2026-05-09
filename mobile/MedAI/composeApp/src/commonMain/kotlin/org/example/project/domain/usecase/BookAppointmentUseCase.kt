package org.example.project.domain.usecase

import org.example.project.domain.repository.DoctorRepository

class BookAppointmentUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(
        doctorId: String,
        slotId: String
    ): Result<String> {
        // Business Logic Validation
        if (slotId.isBlank()) return Result.failure(Exception("Time slot is required"))

        return repository.bookAppointment(doctorId, slotId)
    }
}
