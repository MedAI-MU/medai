package org.example.project.presentation.chatScreen


import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.usecase.chat.GetChatMessagesUseCase
import org.example.project.domain.usecase.chat.ObserveTypingUseCase
import org.example.project.domain.usecase.chat.SendMessageUseCase

class ChatViewModel(
    private val doctorId: String,
    doctorName: String,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeTypingUseCase: ObserveTypingUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ChatState(doctorName = doctorName))
    val state = _state.asStateFlow()

    private val _effect = Channel<ChatEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        getChatMessagesUseCase(doctorId).onEach { list -> _state.update { it.copy(messages = list) } }.launchIn(screenModelScope)
        observeTypingUseCase(doctorId).onEach { isTyping -> _state.update { it.copy(isDoctorTyping = isTyping) } }.launchIn(screenModelScope)
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.MessageInputChanged -> _state.update { it.copy(messageInput = event.text) }
            is ChatEvent.SendMessageClicked -> sendMessage()
            is ChatEvent.BackClicked -> sendEffect(ChatEffect.NavigateBack)
        }
    }

    private fun sendMessage() {
        val text = _state.value.messageInput
        if (text.isBlank()) return
        screenModelScope.launch {
            _state.update { it.copy(messageInput = "") }
            sendMessageUseCase(doctorId, text).onFailure { error -> sendEffect(ChatEffect.ShowError(error.message ?: "Failed")) }
        }
    }

    private fun sendEffect(effect: ChatEffect) {
        screenModelScope.launch { _effect.send(effect) }
    }
}
