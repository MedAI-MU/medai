package org.example.project.domain.usecase.appointment

import kotlinx.datetime.LocalDate
import org.example.project.domain.model.appointment.TimeSlot
import org.example.project.domain.repository.doctor.DoctorRepository

class GetAvailableSlotsUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(doctorId: String, date: LocalDate): Result<List<TimeSlot>> {
        return repository.getAvailableSlots(doctorId, date)
    }
}
