package org.example.project.data.remote.dto.schedule

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleTemplateSlotDto(
    val weekDay: Int,
    val startTime: String,
    val endTime: String
)

@Serializable
data class ScheduleTemplateDoctorDto(
    val id: Int,
    val name: String,
    val specialty: String
)

@Serializable
data class ScheduleTemplateCreatedByDto(
    val id: Int,
    val name: String
)

@Serializable
data class ScheduleTemplateResponseDto(
    val id: Int,
    val name: String,
    val doctor: ScheduleTemplateDoctorDto,
    val slots: List<ScheduleTemplateSlotDto>,
    val createdBy: ScheduleTemplateCreatedByDto,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateScheduleTemplateRequestDto(
    val name: String,
    val slots: List<ScheduleTemplateSlotDto>
)

@Serializable
data class UpdateScheduleTemplateRequestDto(
    val name: String? = null,
    val slots: List<ScheduleTemplateSlotDto>? = null
)

@Serializable
data class ApplyTemplateRequestDto(
    val startDate: String,
    val endDate: String
)
