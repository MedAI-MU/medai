package org.example.project.domain.model.schedule

data class ScheduleDay(
    val day: String,
    val slots: List<ScheduleSlot>
)
