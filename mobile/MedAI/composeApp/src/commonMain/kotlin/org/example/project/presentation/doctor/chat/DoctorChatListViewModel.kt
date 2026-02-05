package org.example.project.presentation.doctor.chat

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.model.ChatConversation
import org.example.project.domain.usecase.GetPatientConversationsUseCase

data class DoctorChatListState(
    val isLoading: Boolean = false,
    val conversations: List<ChatConversation> = emptyList(),
    val error: String? = null
)

sealed class DoctorChatListEvent {
    data class ConversationClicked(val patientId: String, val patientName: String) : DoctorChatListEvent()
}

sealed class DoctorChatListEffect {
    data class NavigateToChat(val patientId: String, val patientName: String) : DoctorChatListEffect()
}

class DoctorChatListViewModel(
    private val getPatientConversationsUseCase: GetPatientConversationsUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(DoctorChatListState())
    val state = _state.asStateFlow()

    private val _effect = Channel<DoctorChatListEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getPatientConversationsUseCase().fold(
                onSuccess = { list -> _state.update { it.copy(isLoading = false, conversations = list) } },
                onFailure = { err -> _state.update { it.copy(isLoading = false, error = err.message) } }
            )
        }
    }

    fun onEvent(event: DoctorChatListEvent) {
        when (event) {
            is DoctorChatListEvent.ConversationClicked -> {
                sendEffect(DoctorChatListEffect.NavigateToChat(event.patientId, event.patientName))
            }
        }
    }

    private fun sendEffect(effect: DoctorChatListEffect) {
        screenModelScope.launch { _effect.send(effect) }
    }
}
