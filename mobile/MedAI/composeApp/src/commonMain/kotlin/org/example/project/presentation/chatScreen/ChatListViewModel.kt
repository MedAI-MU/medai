package org.example.project.presentation.chatScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.usecase.chat.GetConversationsUseCase

class ChatListViewModel(
    private val getConversationsUseCase: GetConversationsUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ChatListState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ChatListEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getConversationsUseCase().fold(
                onSuccess = { list -> _state.update { it.copy(isLoading = false, conversations = list) } },
                onFailure = { err -> _state.update { it.copy(isLoading = false, error = err.message) } }
            )
        }
    }

    fun onEvent(event: ChatListEvent) {
        when (event) {
            is ChatListEvent.ConversationClicked -> {
                sendEffect(ChatListEffect.NavigateToChat(event.doctorId, event.doctorName))
            }
        }
    }

    private fun sendEffect(effect: ChatListEffect) {
        screenModelScope.launch { _effect.send(effect) }
    }
}
