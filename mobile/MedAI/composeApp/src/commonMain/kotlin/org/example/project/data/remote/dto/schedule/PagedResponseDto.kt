package org.example.project.data.remote.dto.schedule

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponseDto<T>(
    val data: List<T>,
    val totalCount: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
