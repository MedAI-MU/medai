package org.example.project.domain.usecase.schedule

import org.example.project.domain.repository.ScheduleRepository

class UpdateScheduleSlotUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        doctorId: Int,
        slotId: Int,
        startTime: String? = null,
        endTime: String? = null,
        dayDate: String? = null
    ): Result<Unit> {
        return repository.updateScheduleSlot(doctorId, slotId, startTime, endTime, dayDate)
    }
}
