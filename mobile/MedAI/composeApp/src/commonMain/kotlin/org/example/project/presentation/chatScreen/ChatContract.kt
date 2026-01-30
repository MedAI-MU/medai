package org.example.project.presentation.chatScreen

import org.example.project.domain.model.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val doctorName: String = "",
    val isDoctorTyping: Boolean = false,
    val messageInput: String = "",
    val isLoading: Boolean = false
)

sealed class ChatEvent {
    data class MessageInputChanged(val text: String) : ChatEvent()
    object SendMessageClicked : ChatEvent()
    object BackClicked : ChatEvent()
}

sealed interface ChatEffect {
    object NavigateBack : ChatEffect
    data class ShowError(val message: String) : ChatEffect
}
