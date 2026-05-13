package org.example.project.domain.usecase.schedule

import org.example.project.domain.model.schedule.ScheduleTemplateSlot
import org.example.project.domain.repository.schedule.ScheduleRepository

class UpdateScheduleTemplateUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        doctorId: Int,
        templateId: Int,
        name: String? = null,
        slots: List<ScheduleTemplateSlot>? = null
    ): Result<Unit> {
        return repository.updateScheduleTemplate(doctorId, templateId, name, slots)
    }
}
