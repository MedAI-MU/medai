package org.example.project.presentation.chatScreen

import org.example.project.domain.model.chat.ChatConversation

data class ChatListState(
    val conversations: List<ChatConversation> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class ChatListEvent {
    data class ConversationClicked(val doctorId: String, val doctorName: String) : ChatListEvent()
}

sealed interface ChatListEffect {
    data class NavigateToChat(val doctorId: String, val doctorName: String) : ChatListEffect
    data class ShowError(val message: String) : ChatListEffect
}
