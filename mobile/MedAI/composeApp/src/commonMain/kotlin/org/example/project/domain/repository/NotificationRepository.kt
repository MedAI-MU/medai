package org.example.project.domain.repository

import org.example.project.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun markAsRead(notificationId: String): Result<Unit>
}
