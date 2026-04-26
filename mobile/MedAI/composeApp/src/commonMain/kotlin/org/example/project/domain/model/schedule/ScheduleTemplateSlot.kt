package org.example.project.domain.model.schedule

data class ScheduleTemplateSlot(
    val weekDay: Int,
    val startTime: String,
    val endTime: String
)
