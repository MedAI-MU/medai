package org.example.project.domain.model.schedule

data class ScheduleTemplate(
    val id: Int,
    val name: String,
    val doctorId: Int,
    val doctorName: String,
    val doctorSpecialty: String,
    val slots: List<ScheduleTemplateSlot>,
    val createdById: Int,
    val createdByName: String,
    val createdAt: String,
    val updatedAt: String
)
