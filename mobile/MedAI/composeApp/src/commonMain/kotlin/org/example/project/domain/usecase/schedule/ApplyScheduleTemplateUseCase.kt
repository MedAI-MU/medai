package org.example.project.domain.usecase.schedule

import org.example.project.domain.repository.schedule.ScheduleRepository

class ApplyScheduleTemplateUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        doctorId: Int,
        templateId: Int,
        startDate: String,
        endDate: String
    ): Result<Unit> {
        return repository.applyTemplate(doctorId, templateId, startDate, endDate)
    }
}
