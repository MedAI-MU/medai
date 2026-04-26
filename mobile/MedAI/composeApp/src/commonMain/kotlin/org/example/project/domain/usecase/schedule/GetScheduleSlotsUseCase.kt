package org.example.project.domain.usecase.schedule

import org.example.project.domain.model.schedule.DoctorSchedule
import org.example.project.domain.repository.ScheduleRepository

class GetScheduleSlotsUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        doctorId: Int,
        fromDate: String? = null,
        toDate: String? = null,
        pageNo: Int = 1,
        pageSize: Int = 10
    ): Result<DoctorSchedule> {
        return repository.getScheduleSlots(doctorId, fromDate, toDate, pageNo, pageSize)
    }
}
