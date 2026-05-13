package org.example.project.presentation.notificationScreen

import org.example.project.domain.model.notification.Notification

data class NotificationState(
    val notifications: List<NotificationUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class NotificationUiModel {
    data class Header(val title: String) : NotificationUiModel()
    data class Item(val notification: Notification) : NotificationUiModel()
}

sealed class NotificationEvent {
    object BackClicked : NotificationEvent()
    data class NotificationClicked(val id: String) : NotificationEvent()
}

sealed interface NotificationEffect {
    object NavigateBack : NotificationEffect
}
