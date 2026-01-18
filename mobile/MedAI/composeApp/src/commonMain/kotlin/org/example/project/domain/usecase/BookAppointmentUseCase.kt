package org.example.project.domain.usecase

import kotlinx.datetime.LocalDate
import org.example.project.domain.repository.DoctorRepository

class BookAppointmentUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(
        doctorId: String,
        slotId: String,
        date: LocalDate,
        patientName: String,
        patientAge: String,
        patientGender: String,
        problemDescription: String
    ): Result<String> {
        // Business Logic Validation
        if (patientName.isBlank()) return Result.failure(Exception("Patient name is required"))
        if (slotId.isBlank()) return Result.failure(Exception("Time slot is required"))

        return repository.bookAppointment(
            doctorId, slotId, date, patientName, patientAge, patientGender, problemDescription
        )
    }
}
