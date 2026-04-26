package org.example.project.domain.usecase.schedule

import org.example.project.domain.model.schedule.ScheduleTemplateSlot
import org.example.project.domain.repository.ScheduleRepository

class CreateScheduleTemplateUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        doctorId: Int,
        name: String,
        slots: List<ScheduleTemplateSlot>
    ): Result<Unit> {
        return repository.createScheduleTemplate(doctorId, name, slots)
    }
}
