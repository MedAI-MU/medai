package org.example.project.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.chat.ChatConversation
import org.example.project.domain.model.chat.Message
import org.example.project.domain.repository.chat.ChatRepository

class GetConversationsUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(): Result<List<ChatConversation>> {
        return repository.getConversations()
    }
}

class GetChatMessagesUseCase(private val repository: ChatRepository) {
    operator fun invoke(doctorId: String): Flow<List<Message>> {
        return repository.getMessages(doctorId)
    }
}

class SendMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(doctorId: String, text: String): Result<Message> {
        if (text.isBlank()) return Result.failure(Exception("Message cannot be empty"))
        return repository.sendMessage(doctorId, text)
    }
}

class ObserveTypingUseCase(private val repository: ChatRepository) {
    operator fun invoke(doctorId: String): Flow<Boolean> {
        return repository.observeDoctorTyping(doctorId)
    }
}
