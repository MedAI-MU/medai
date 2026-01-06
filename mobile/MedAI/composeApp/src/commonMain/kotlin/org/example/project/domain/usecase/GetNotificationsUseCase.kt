package org.example.project.domain.usecase

import org.example.project.domain.model.Notification
import org.example.project.domain.repository.NotificationRepository

class GetNotificationsUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): Result<List<Notification>> {
        return repository.getNotifications()
    }
}
