package org.example.project.presentation.chatScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.chat.GetChatMessagesUseCase
import org.example.project.domain.usecase.chat.ObserveTypingUseCase
import org.example.project.domain.usecase.chat.SendMessageUseCase

class ChatViewModel(
    private val doctorId: String,
    doctorName: String,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeTypingUseCase: ObserveTypingUseCase
) : MviScreenModel<ChatState, ChatEvent, ChatEffect>(ChatState(doctorName = doctorName)) {

    init {
        getChatMessagesUseCase(doctorId).onEach { list -> setState { copy(messages = list) } }.launchIn(screenModelScope)
        observeTypingUseCase(doctorId).onEach { isTyping -> setState { copy(isDoctorTyping = isTyping) } }.launchIn(screenModelScope)
    }

    override fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.MessageInputChanged -> setState { copy(messageInput = event.text) }
            is ChatEvent.SendMessageClicked -> sendMessage()
            is ChatEvent.BackClicked -> sendEffect(ChatEffect.NavigateBack)
        }
    }

    private fun sendMessage() {
        val text = state.value.messageInput
        if (text.isBlank()) return
        screenModelScope.launch {
            setState { copy(messageInput = "") }
            sendMessageUseCase(doctorId, text).onFailure { error -> sendEffect(ChatEffect.ShowError(error.message ?: "Failed")) }
        }
    }
}
