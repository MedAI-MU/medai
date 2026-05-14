package org.example.project.domain.model.appointment

data class TimeSlot(
    val id: String,
    val time: String,
    val isAvailable: Boolean
)
