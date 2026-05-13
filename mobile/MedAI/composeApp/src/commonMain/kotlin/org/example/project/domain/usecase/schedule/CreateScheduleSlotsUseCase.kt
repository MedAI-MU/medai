package org.example.project.domain.usecase.schedule

import org.example.project.domain.repository.schedule.CreateScheduleDayInput
import org.example.project.domain.repository.schedule.ScheduleRepository

class CreateScheduleSlotsUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        doctorId: Int,
        days: List<CreateScheduleDayInput>
    ): Result<Unit> {
        return repository.createScheduleSlots(doctorId, days)
    }
}
