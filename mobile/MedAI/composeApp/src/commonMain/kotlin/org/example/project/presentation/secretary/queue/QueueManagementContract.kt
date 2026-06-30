package org.example.project.presentation.secretary.queue

import org.example.project.domain.model.secretary.QueueEntry

data class QueueManagementState(
    val queues: List<QueueEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class QueueManagementEvent {
    object LoadQueues : QueueManagementEvent()
    data class CheckIn(val appointmentId: String) : QueueManagementEvent()
}

sealed class QueueManagementEffect {
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : QueueManagementEffect()
}
