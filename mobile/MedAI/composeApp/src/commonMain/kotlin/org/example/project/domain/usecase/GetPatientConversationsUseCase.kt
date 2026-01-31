package org.example.project.domain.usecase

import org.example.project.domain.model.ChatConversation
import org.example.project.domain.repository.ChatRepository

class GetPatientConversationsUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(): Result<List<ChatConversation>> {
        return repository.getPatientConversations()
    }
}
