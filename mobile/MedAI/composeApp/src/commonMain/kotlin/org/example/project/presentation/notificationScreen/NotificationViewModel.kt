package org.example.project.presentation.notificationScreen


import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.example.project.domain.model.notification.Notification
import org.example.project.domain.usecase.notification.GetNotificationsUseCase

class NotificationViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    private val _effect = Channel<NotificationEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadNotifications()
    }

    fun onEvent(event: NotificationEvent) {
        when (event) {
            NotificationEvent.BackClicked -> sendEffect(NotificationEffect.NavigateBack)
            is NotificationEvent.NotificationClicked -> {
                // In production: Mark as read via UseCase here
            }
        }
    }

    private fun loadNotifications() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getNotificationsUseCase().fold(
                onSuccess = { list ->
                    val groupedList = groupNotifications(list)
                    _state.update { it.copy(isLoading = false, notifications = groupedList) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
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

    private fun sendEffect(effect: NotificationEffect) {
        screenModelScope.launch { _effect.send(effect) }
    }
}
