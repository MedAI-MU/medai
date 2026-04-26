package org.example.project.domain.usecase.schedule

import org.example.project.domain.repository.ScheduleRepository

class DeleteScheduleTemplateUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(doctorId: Int, templateId: Int): Result<Unit> {
        return repository.deleteScheduleTemplate(doctorId, templateId)
    }
}
