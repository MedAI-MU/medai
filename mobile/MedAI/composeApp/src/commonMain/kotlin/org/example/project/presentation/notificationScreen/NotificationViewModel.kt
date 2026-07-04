package org.example.project.presentation.notificationScreen


import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.model.notification.Notification
import org.example.project.domain.usecase.notification.GetNotificationsUseCase

class NotificationViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase
) : MviScreenModel<NotificationState, NotificationEvent, NotificationEffect>(NotificationState()) {

    init {
        loadNotifications()
    }

    override fun onEvent(event: NotificationEvent) {
        when (event) {
            NotificationEvent.BackClicked -> sendEffect(NotificationEffect.NavigateBack)
            is NotificationEvent.NotificationClicked -> {
                // In production: Mark as read via UseCase here
            }
        }
    }

    private fun loadNotifications() {
        screenModelScope.launch {
            setState { copy(isLoading = true) }
            getNotificationsUseCase().fold(
                onSuccess = { list ->
                    val groupedList = groupNotifications(list)
                    setState { copy(isLoading = false, notifications = groupedList) }
                },
                onFailure = { error ->
                    setState { copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun groupNotifications(list: List<Notification>): List<NotificationUiModel> {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val yesterday = today.minus(DatePeriod(days = 1))


        val grouped = list.groupBy { notification ->
            val date = notification.timestamp.toLocalDateTime(timeZone).date
            when (date) {
                today -> "Today"
                yesterday -> "Yesterday"
                else -> "${date.dayOfMonth} ${date.month.name.lowercase().replaceFirstChar { it.uppercase() }}"
            }
        }

        val result = mutableListOf<NotificationUiModel>()


        val sortedKeys = grouped.keys.sortedWith { key1, key2 ->
            when {
                key1 == "Today" -> -1
                key2 == "Today" -> 1
                key1 == "Yesterday" -> -1
                key2 == "Yesterday" -> 1
                else -> 0
            }
        }

        sortedKeys.forEach { key ->
            result.add(NotificationUiModel.Header(key))
            val items = grouped[key]?.sortedByDescending { it.timestamp } ?: emptyList()
            result.addAll(items.map { NotificationUiModel.Item(it) })
        }

        return result
    }
}
