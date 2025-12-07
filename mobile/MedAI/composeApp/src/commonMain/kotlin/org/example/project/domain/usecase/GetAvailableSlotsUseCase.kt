package org.example.project.domain.usecase

import kotlinx.datetime.LocalDate
import org.example.project.domain.model.TimeSlot
import org.example.project.domain.repository.DoctorRepository

class GetAvailableSlotsUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(doctorId: String, date: LocalDate): Result<List<TimeSlot>> {
        return repository.getAvailableSlots(doctorId, date)
    }
}
