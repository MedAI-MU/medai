package org.example.project.domain.repository

import org.example.project.domain.model.schedule.DoctorSchedule
import org.example.project.domain.model.schedule.PagedTemplates
import org.example.project.domain.model.schedule.ScheduleTemplateSlot

interface ScheduleRepository {

    // --- Templates ---

    suspend fun getScheduleTemplates(
        pageNo: Int = 1,
        pageSize: Int = 10,
        name: String? = null,
        doctorId: Int? = null
    ): Result<PagedTemplates>

    suspend fun createScheduleTemplate(
        doctorId: Int,
        name: String,
        slots: List<ScheduleTemplateSlot>
    ): Result<Unit>

    suspend fun updateScheduleTemplate(
        doctorId: Int,
        templateId: Int,
        name: String? = null,
        slots: List<ScheduleTemplateSlot>? = null
    ): Result<Unit>

    suspend fun deleteScheduleTemplate(
        doctorId: Int,
        templateId: Int
    ): Result<Unit>

    suspend fun applyTemplate(
        doctorId: Int,
        templateId: Int,
        startDate: String,
        endDate: String
    ): Result<Unit>

    // --- Slots ---

    suspend fun getScheduleSlots(
        doctorId: Int,
        fromDate: String? = null,
        toDate: String? = null,
        pageNo: Int = 1,
        pageSize: Int = 10
    ): Result<DoctorSchedule>

    suspend fun createScheduleSlots(
        doctorId: Int,
        days: List<CreateScheduleDayInput>
    ): Result<Unit>

    suspend fun updateScheduleSlot(
        doctorId: Int,
        slotId: Int,
        startTime: String? = null,
        endTime: String? = null,
        dayDate: String? = null
    ): Result<Unit>

    suspend fun deleteScheduleSlot(
        doctorId: Int,
        slotId: Int
    ): Result<Unit>
}

data class CreateScheduleDayInput(
    val date: String,
    val slots: List<CreateScheduleSlotInput>
)

data class CreateScheduleSlotInput(
    val startTime: String,
    val endTime: String
)
