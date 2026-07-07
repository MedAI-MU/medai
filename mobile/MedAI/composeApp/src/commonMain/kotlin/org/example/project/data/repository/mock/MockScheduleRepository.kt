package org.example.project.data.repository.mock

import org.example.project.domain.model.schedule.*
import org.example.project.domain.repository.schedule.CreateScheduleDayInput
import org.example.project.domain.repository.schedule.ScheduleRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

class MockScheduleRepository : ScheduleRepository {

    private val templates = mutableListOf(
        ScheduleTemplate(
            id = 1,
            name = "Morning Clinic Schedule",
            doctorId = 1,
            doctorName = "Dr. Ahmad Hassan",
            doctorSpecialty = "General Medicine",
            slots = listOf(
                ScheduleTemplateSlot(weekDay = 0, startTime = "08:00", endTime = "09:00"),
                ScheduleTemplateSlot(weekDay = 0, startTime = "09:00", endTime = "10:00"),
                ScheduleTemplateSlot(weekDay = 0, startTime = "10:00", endTime = "11:00"),
                ScheduleTemplateSlot(weekDay = 1, startTime = "08:00", endTime = "09:00"),
                ScheduleTemplateSlot(weekDay = 1, startTime = "09:00", endTime = "10:00"),
                ScheduleTemplateSlot(weekDay = 2, startTime = "08:00", endTime = "09:00"),
                ScheduleTemplateSlot(weekDay = 2, startTime = "09:00", endTime = "10:00"),
            ),
            createdById = 1,
            createdByName = "Dr. Ahmad Hassan",
            createdAt = "2026-01-15T10:00:00Z",
            updatedAt = "2026-01-15T10:00:00Z"
        ),
        ScheduleTemplate(
            id = 2,
            name = "Evening Clinic Schedule",
            doctorId = 1,
            doctorName = "Dr. Ahmad Hassan",
            doctorSpecialty = "General Medicine",
            slots = listOf(
                ScheduleTemplateSlot(weekDay = 3, startTime = "16:00", endTime = "17:00"),
                ScheduleTemplateSlot(weekDay = 3, startTime = "17:00", endTime = "18:00"),
                ScheduleTemplateSlot(weekDay = 4, startTime = "16:00", endTime = "17:00"),
                ScheduleTemplateSlot(weekDay = 4, startTime = "17:00", endTime = "18:00"),
                ScheduleTemplateSlot(weekDay = 5, startTime = "14:00", endTime = "15:00"),
            ),
            createdById = 2,
            createdByName = "Secretary Sara",
            createdAt = "2026-01-20T14:00:00Z",
            updatedAt = "2026-01-22T09:00:00Z"
        ),
        ScheduleTemplate(
            id = 3,
            name = "Weekend Short Shift",
            doctorId = 1,
            doctorName = "Dr. Ahmad Hassan",
            doctorSpecialty = "General Medicine",
            slots = listOf(
                ScheduleTemplateSlot(weekDay = 6, startTime = "10:00", endTime = "11:00"),
                ScheduleTemplateSlot(weekDay = 6, startTime = "11:00", endTime = "12:00"),
            ),
            createdById = 1,
            createdByName = "Dr. Ahmad Hassan",
            createdAt = "2026-02-01T08:00:00Z",
            updatedAt = "2026-02-01T08:00:00Z"
        )
    )

    private var nextTemplateId = 4

    private val scheduleSlots = mutableListOf(
        MockScheduleDay(
            day = "2026-02-16",
            slots = mutableListOf(
                MockSlot(id = 1, startTime = "08:00", endTime = "09:00", status = SlotStatus.AVAILABLE),
                MockSlot(id = 2, startTime = "09:00", endTime = "10:00", status = SlotStatus.BOOKED),
                MockSlot(id = 3, startTime = "10:00", endTime = "11:00", status = SlotStatus.AVAILABLE),
            )
        ),
        MockScheduleDay(
            day = "2026-02-17",
            slots = mutableListOf(
                MockSlot(id = 4, startTime = "08:00", endTime = "09:00", status = SlotStatus.COMPLETED),
                MockSlot(id = 5, startTime = "09:00", endTime = "10:00", status = SlotStatus.AVAILABLE),
                MockSlot(id = 6, startTime = "14:00", endTime = "15:00", status = SlotStatus.AVAILABLE),
            )
        ),
        MockScheduleDay(
            day = "2026-02-18",
            slots = mutableListOf(
                MockSlot(id = 7, startTime = "08:00", endTime = "09:00", status = SlotStatus.AVAILABLE),
                MockSlot(id = 8, startTime = "09:00", endTime = "10:00", status = SlotStatus.CANCELED),
                MockSlot(id = 9, startTime = "10:00", endTime = "11:00", status = SlotStatus.BOOKED),
                MockSlot(id = 10, startTime = "16:00", endTime = "17:00", status = SlotStatus.AVAILABLE),
            )
        ),
        MockScheduleDay(
            day = "2026-02-19",
            slots = mutableListOf(
                MockSlot(id = 11, startTime = "09:00", endTime = "10:00", status = SlotStatus.AVAILABLE),
                MockSlot(id = 12, startTime = "10:00", endTime = "11:00", status = SlotStatus.AVAILABLE),
            )
        )
    )

    private var nextSlotId = 13

    // --- Templates ---

    override suspend fun getScheduleTemplates(
        pageNo: Int,
        pageSize: Int,
        name: String?,
        doctorId: Int?
    ): Result<PagedTemplates> {
        var filtered = templates.toList()
        if (name != null) {
            filtered = filtered.filter { it.name.contains(name, ignoreCase = true) }
        }
        if (doctorId != null) {
            filtered = filtered.filter { it.doctorId == doctorId }
        }
        val start = (pageNo - 1) * pageSize
        val paged = filtered.drop(start).take(pageSize)
        return Result.success(
            PagedTemplates(
                data = paged,
                totalCount = filtered.size,
                currentPage = pageNo,
                pageSize = pageSize,
                hasNext = start + pageSize < filtered.size,
                hasPrevious = pageNo > 1
            )
        )
    }

    override suspend fun createScheduleTemplate(
        doctorId: Int,
        name: String,
        slots: List<ScheduleTemplateSlot>
    ): Result<Unit> {
        templates.add(
            ScheduleTemplate(
                id = nextTemplateId++,
                name = name,
                doctorId = doctorId,
                doctorName = "Dr. Ahmad Hassan",
                doctorSpecialty = "General Medicine",
                slots = slots,
                createdById = doctorId,
                createdByName = "Current User",
                createdAt = "2026-02-15T12:00:00Z",
                updatedAt = "2026-02-15T12:00:00Z"
            )
        )
        return Result.success(Unit)
    }

    override suspend fun updateScheduleTemplate(
        doctorId: Int,
        templateId: Int,
        name: String?,
        slots: List<ScheduleTemplateSlot>?
    ): Result<Unit> {
        val index = templates.indexOfFirst { it.id == templateId }
        if (index == -1) return Result.failure(Exception("Template not found"))
        val existing = templates[index]
        templates[index] = existing.copy(
            name = name ?: existing.name,
            slots = slots ?: existing.slots,
            updatedAt = "2026-02-15T12:00:00Z"
        )
        return Result.success(Unit)
    }

    override suspend fun deleteScheduleTemplate(
        doctorId: Int,
        templateId: Int
    ): Result<Unit> {
        val removed = templates.removeAll { it.id == templateId }
        return if (removed) Result.success(Unit)
        else Result.failure(Exception("Template not found"))
    }

    override suspend fun applyTemplate(
        doctorId: Int,
        templateId: Int,
        startDate: String,
        endDate: String
    ): Result<Unit> {
        val template = templates.find { it.id == templateId } ?: return Result.failure(Exception("Template not found"))

        val start = try { LocalDate.parse(startDate) } catch(e: Exception) { return Result.failure(e) }
        val end = try { LocalDate.parse(endDate) } catch(e: Exception) { return Result.failure(e) }

        if (start > end) return Result.failure(Exception("Invalid range"))

        var current = start
        while (current <= end) {
            val weekDayInt = when (current.dayOfWeek) {
                DayOfWeek.SUNDAY -> 0
                DayOfWeek.MONDAY -> 1
                DayOfWeek.TUESDAY -> 2
                DayOfWeek.WEDNESDAY -> 3
                DayOfWeek.THURSDAY -> 4
                DayOfWeek.FRIDAY -> 5
                DayOfWeek.SATURDAY -> 6
                else -> 0
            }

            val dayTemplateSlots = template.slots.filter { it.weekDay == weekDayInt }
            if (dayTemplateSlots.isNotEmpty()) {
                val dateStr = current.toString()
                val existingDay = scheduleSlots.find { it.day == dateStr }
                if (existingDay != null) {
                    dayTemplateSlots.forEach { templateSlot ->
                        val isOverlap = existingDay.slots.any {
                            it.startTime < templateSlot.endTime && templateSlot.startTime < it.endTime
                        }
                        if (!isOverlap) {
                            existingDay.slots.add(
                                MockSlot(
                                    id = nextSlotId++,
                                    startTime = templateSlot.startTime,
                                    endTime = templateSlot.endTime,
                                    status = SlotStatus.AVAILABLE
                                )
                            )
                        }
                    }
                    existingDay.slots.sortBy { it.startTime }
                } else {
                    val newSlots = dayTemplateSlots.map { templateSlot ->
                        MockSlot(
                            id = nextSlotId++,
                            startTime = templateSlot.startTime,
                            endTime = templateSlot.endTime,
                            status = SlotStatus.AVAILABLE
                        )
                    }.toMutableList()
                    scheduleSlots.add(
                        MockScheduleDay(
                            day = dateStr,
                            slots = newSlots
                        )
                    )
                }
            }
            current = current.plus(1, DateTimeUnit.DAY)
        }

        scheduleSlots.sortBy { it.day }
        return Result.success(Unit)
    }

    // --- Slots ---

    override suspend fun getScheduleSlots(
        doctorId: Int,
        fromDate: String?,
        toDate: String?,
        pageNo: Int,
        pageSize: Int
    ): Result<DoctorSchedule> {
        var filtered = scheduleSlots.toList()
        if (fromDate != null) {
            filtered = filtered.filter { it.day >= fromDate }
        }
        if (toDate != null) {
            filtered = filtered.filter { it.day <= toDate }
        }
        val start = (pageNo - 1) * pageSize
        val paged = filtered.drop(start).take(pageSize)
        return Result.success(
            DoctorSchedule(
                doctorId = doctorId,
                name = "Dr. Ahmad Hassan",
                speciality = "General Medicine",
                days = paged.map { day ->
                    ScheduleDay(
                        day = day.day,
                        slots = day.slots.map { slot ->
                            ScheduleSlot(
                                id = slot.id,
                                startTime = slot.startTime,
                                endTime = slot.endTime,
                                status = slot.status
                            )
                        }
                    )
                },
                totalCount = filtered.size,
                currentPage = pageNo,
                pageSize = pageSize,
                hasNext = start + pageSize < filtered.size,
                hasPrevious = pageNo > 1
            )
        )
    }

    override suspend fun createScheduleSlots(
        doctorId: Int,
        days: List<CreateScheduleDayInput>
    ): Result<Unit> {
        for (dayInput in days) {
            val existing = scheduleSlots.find { it.day == dayInput.date }
            if (existing != null) {
                existing.slots.addAll(dayInput.slots.map { slot ->
                    MockSlot(
                        id = nextSlotId++,
                        startTime = slot.startTime,
                        endTime = slot.endTime,
                        status = SlotStatus.AVAILABLE
                    )
                })
            } else {
                scheduleSlots.add(
                    MockScheduleDay(
                        day = dayInput.date,
                        slots = dayInput.slots.map { slot ->
                            MockSlot(
                                id = nextSlotId++,
                                startTime = slot.startTime,
                                endTime = slot.endTime,
                                status = SlotStatus.AVAILABLE
                            )
                        }.toMutableList()
                    )
                )
            }
        }
        return Result.success(Unit)
    }

    override suspend fun updateScheduleSlot(
        doctorId: Int,
        slotId: Int,
        startTime: String?,
        endTime: String?,
        dayDate: String?
    ): Result<Unit> {
        for (day in scheduleSlots) {
            val slotIndex = day.slots.indexOfFirst { it.id == slotId }
            if (slotIndex != -1) {
                val existing = day.slots[slotIndex]
                day.slots[slotIndex] = existing.copy(
                    startTime = startTime ?: existing.startTime,
                    endTime = endTime ?: existing.endTime
                )
                return Result.success(Unit)
            }
        }
        return Result.failure(Exception("Slot not found"))
    }

    override suspend fun deleteScheduleSlot(
        doctorId: Int,
        slotId: Int
    ): Result<Unit> {
        for (day in scheduleSlots) {
            val removed = day.slots.removeAll { it.id == slotId && it.status == SlotStatus.AVAILABLE }
            if (removed) return Result.success(Unit)
        }
        return Result.failure(Exception("Slot not found or not available"))
    }
}

private data class MockScheduleDay(
    val day: String,
    val slots: MutableList<MockSlot>
)

private data class MockSlot(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val status: SlotStatus
)
