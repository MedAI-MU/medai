package org.example.project.domain.usecase.schedule

import org.example.project.domain.model.schedule.PagedTemplates
import org.example.project.domain.repository.schedule.ScheduleRepository

class GetScheduleTemplatesUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        pageNo: Int = 1,
        pageSize: Int = 10,
        name: String? = null,
        doctorId: Int? = null
    ): Result<PagedTemplates> {
        return repository.getScheduleTemplates(pageNo, pageSize, name, doctorId)
    }
}
