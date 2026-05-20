package org.example.project.domain.usecase.notification

import org.example.project.domain.model.notification.Notification
import org.example.project.domain.repository.notification.NotificationRepository

class GetNotificationsUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): Result<List<Notification>> {
        return repository.getNotifications()
    }
}
