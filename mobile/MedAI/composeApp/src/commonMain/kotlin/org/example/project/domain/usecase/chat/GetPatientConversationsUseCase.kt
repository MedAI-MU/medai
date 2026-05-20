package org.example.project.domain.usecase.chat

import org.example.project.domain.model.chat.ChatConversation
import org.example.project.domain.repository.chat.ChatRepository

class GetPatientConversationsUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(): Result<List<ChatConversation>> {
        return repository.getPatientConversations()
    }
}
