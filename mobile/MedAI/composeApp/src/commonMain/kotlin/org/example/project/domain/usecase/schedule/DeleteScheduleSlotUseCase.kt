package org.example.project.domain.usecase.schedule

import org.example.project.domain.repository.schedule.ScheduleRepository

class DeleteScheduleSlotUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(doctorId: Int, slotId: Int): Result<Unit> {
        return repository.deleteScheduleSlot(doctorId, slotId)
    }
}
