package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.data.remote.dto.schedule.*
import org.example.project.domain.model.schedule.*
import org.example.project.domain.repository.CreateScheduleDayInput
import org.example.project.domain.repository.ScheduleRepository

class NetworkScheduleRepository(
    private val client: HttpClient
) : ScheduleRepository {

    // --- Templates ---

    override suspend fun getScheduleTemplates(
        pageNo: Int,
        pageSize: Int,
        name: String?,
        doctorId: Int?
    ): Result<PagedTemplates> {
        return try {
            val response: PagedResponseDto<ScheduleTemplateResponseDto> =
                client.get("/api/doctors/schedule-templates") {
                    parameter("pageNo", pageNo)
                    parameter("pageSize", pageSize)
                    if (name != null) parameter("name", name)
                    if (doctorId != null) parameter("doctorId", doctorId)
                }.body()

            Result.success(
                PagedTemplates(
                    data = response.data.map { it.toDomain() },
                    totalCount = response.totalCount,
                    currentPage = response.currentPage,
                    pageSize = response.pageSize,
                    hasNext = response.hasNext,
                    hasPrevious = response.hasPrevious
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createScheduleTemplate(
        doctorId: Int,
        name: String,
        slots: List<ScheduleTemplateSlot>
    ): Result<Unit> {
        return try {
            client.post("/api/doctors/$doctorId/schedule-templates") {
                contentType(ContentType.Application.Json)
                setBody(
                    CreateScheduleTemplateRequestDto(
                        name = name,
                        slots = slots.map {
                            ScheduleTemplateSlotDto(
                                weekDay = it.weekDay,
                                startTime = it.startTime,
                                endTime = it.endTime
                            )
                        }
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateScheduleTemplate(
        doctorId: Int,
        templateId: Int,
        name: String?,
        slots: List<ScheduleTemplateSlot>?
    ): Result<Unit> {
        return try {
            client.patch("/api/doctors/$doctorId/schedule-templates/$templateId") {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateScheduleTemplateRequestDto(
                        name = name,
                        slots = slots?.map {
                            ScheduleTemplateSlotDto(
                                weekDay = it.weekDay,
                                startTime = it.startTime,
                                endTime = it.endTime
                            )
                        }
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteScheduleTemplate(
        doctorId: Int,
        templateId: Int
    ): Result<Unit> {
        return try {
            client.delete("/api/doctors/$doctorId/schedule-templates/$templateId")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyTemplate(
        doctorId: Int,
        templateId: Int,
        startDate: String,
        endDate: String
    ): Result<Unit> {
        return try {
            client.post("/api/doctors/$doctorId/schedule-templates/$templateId/apply") {
                contentType(ContentType.Application.Json)
                setBody(ApplyTemplateRequestDto(startDate = startDate, endDate = endDate))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Slots ---

    override suspend fun getScheduleSlots(
        doctorId: Int,
        fromDate: String?,
        toDate: String?,
        pageNo: Int,
        pageSize: Int
    ): Result<DoctorSchedule> {
        return try {
            val response: DocScheduleResponseDto =
                client.get("/api/doctors/$doctorId/schedule-slots") {
                    if (fromDate != null) parameter("fromDate", fromDate)
                    if (toDate != null) parameter("toDate", toDate)
                    parameter("pageNo", pageNo)
                    parameter("pageSize", pageSize)
                }.body()

            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createScheduleSlots(
        doctorId: Int,
        days: List<CreateScheduleDayInput>
    ): Result<Unit> {
        return try {
            client.post("/api/doctors/$doctorId/schedule-slots") {
                contentType(ContentType.Application.Json)
                setBody(
                    CreateDocScheduleRequestDto(
                        days = days.map { day ->
                            CreateDocScheduleDayRequestDto(
                                date = day.date,
                                slots = day.slots.map { slot ->
                                    CreateDocScheduleSlotRequestDto(
                                        startTime = slot.startTime,
                                        endTime = slot.endTime
                                    )
                                }
                            )
                        }
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateScheduleSlot(
        doctorId: Int,
        slotId: Int,
        startTime: String?,
        endTime: String?,
        dayDate: String?
    ): Result<Unit> {
        return try {
            client.patch("/api/doctors/$doctorId/schedule-slots/$slotId") {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateDocScheduleSlotRequestDto(
                        startTime = startTime,
                        endTime = endTime,
                        dayDate = dayDate
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteScheduleSlot(
        doctorId: Int,
        slotId: Int
    ): Result<Unit> {
        return try {
            client.delete("/api/doctors/$doctorId/schedule-slots/$slotId")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// --- Mappers ---

private fun ScheduleTemplateResponseDto.toDomain() = ScheduleTemplate(
    id = id,
    name = name,
    doctorId = doctor.id,
    doctorName = doctor.name,
    doctorSpecialty = doctor.specialty,
    slots = slots.map {
        ScheduleTemplateSlot(
            weekDay = it.weekDay,
            startTime = it.startTime,
            endTime = it.endTime
        )
    },
    createdById = createdBy.id,
    createdByName = createdBy.name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun DocScheduleResponseDto.toDomain() = DoctorSchedule(
    doctorId = doctorId,
    name = name,
    speciality = speciality,
    days = days.data.map { dayDto ->
        ScheduleDay(
            day = dayDto.day,
            slots = dayDto.slots.map { slotDto ->
                ScheduleSlot(
                    id = slotDto.id,
                    startTime = slotDto.startTime,
                    endTime = slotDto.endTime,
                    status = when (slotDto.status.lowercase()) {
                        "booked" -> SlotStatus.BOOKED
                        "canceled" -> SlotStatus.CANCELED
                        "completed" -> SlotStatus.COMPLETED
                        else -> SlotStatus.AVAILABLE
                    }
                )
            }
        )
    },
    totalCount = days.totalCount,
    currentPage = days.currentPage,
    pageSize = days.pageSize,
    hasNext = days.hasNext,
    hasPrevious = days.hasPrevious
)
