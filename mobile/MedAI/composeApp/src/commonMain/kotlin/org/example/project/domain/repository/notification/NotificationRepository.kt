package org.example.project.domain.repository.notification

import org.example.project.domain.model.notification.Notification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun markAsRead(notificationId: String): Result<Unit>
}
