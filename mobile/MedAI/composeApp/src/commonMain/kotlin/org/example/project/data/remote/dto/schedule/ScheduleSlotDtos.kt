package org.example.project.data.remote.dto.schedule

import kotlinx.serialization.Serializable

@Serializable
data class DocScheduleSlotResponseDto(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val status: String,
    val schedule: ScheduleDto? = null
)

@Serializable
data class ScheduleDto(
    val id: Int,
    val dayDate: String
)

@Serializable
data class DocScheduleDayResponseDto(
    val day: String,
    val slots: List<DocScheduleSlotResponseDto>
)

@Serializable
data class DocScheduleDaysPagedDto(
    val data: List<DocScheduleDayResponseDto>,
    val totalCount: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

@Serializable
data class DocScheduleResponseDto(
    val doctorId: Int,
    val name: String,
    val speciality: String,
    val days: DocScheduleDaysPagedDto
)

@Serializable
data class CreateDocScheduleSlotRequestDto(
    val startTime: String,
    val endTime: String
)

@Serializable
data class CreateDocScheduleDayRequestDto(
    val date: String,
    val slots: List<CreateDocScheduleSlotRequestDto>
)

@Serializable
data class CreateDocScheduleRequestDto(
    val days: List<CreateDocScheduleDayRequestDto>
)

@Serializable
data class UpdateDocScheduleSlotRequestDto(
    val startTime: String? = null,
    val endTime: String? = null,
    val dayDate: String? = null
)
