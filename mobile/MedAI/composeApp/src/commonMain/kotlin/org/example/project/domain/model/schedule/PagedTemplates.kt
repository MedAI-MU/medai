package org.example.project.domain.model.schedule

data class PagedTemplates(
    val data: List<ScheduleTemplate>,
    val totalCount: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
