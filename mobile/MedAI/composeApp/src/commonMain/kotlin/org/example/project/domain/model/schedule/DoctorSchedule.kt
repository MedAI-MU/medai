package org.example.project.domain.model.schedule

data class DoctorSchedule(
    val doctorId: Int,
    val name: String,
    val speciality: String,
    val days: List<ScheduleDay>,
    val totalCount: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
