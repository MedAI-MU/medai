package org.example.project.presentation.chatScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.chat.GetConversationsUseCase

class ChatListViewModel(
    private val getConversationsUseCase: GetConversationsUseCase
) : MviScreenModel<ChatListState, ChatListEvent, ChatListEffect>(ChatListState()) {

    init {
        loadConversations()
    }

    private fun loadConversations() {
        screenModelScope.launch {
            setState { copy(isLoading = true) }
            getConversationsUseCase().fold(
                onSuccess = { list -> setState { copy(isLoading = false, conversations = list) } },
                onFailure = { err -> setState { copy(isLoading = false, error = err.message) } }
            )
        }
    }

    override fun onEvent(event: ChatListEvent) {
        when (event) {
            is ChatListEvent.ConversationClicked -> {
                sendEffect(ChatListEffect.NavigateToChat(event.doctorId, event.doctorName))
            }
        }
    }
}
