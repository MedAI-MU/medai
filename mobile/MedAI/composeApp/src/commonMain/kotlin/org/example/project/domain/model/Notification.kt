package org.example.project.domain.model

import kotlinx.datetime.Instant

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Instant,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    APPOINTMENT_CONFIRMED,
    APPOINTMENT_CANCELLED,
    SCHEDULE_CHANGED,
    GENERAL_INFO
}
