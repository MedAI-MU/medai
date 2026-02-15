package org.example.project.domain.model.schedule

data class ScheduleSlot(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val status: SlotStatus
)
