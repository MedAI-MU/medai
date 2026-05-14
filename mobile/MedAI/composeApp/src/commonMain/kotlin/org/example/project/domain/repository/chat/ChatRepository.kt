package org.example.project.domain.repository.chat

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.chat.ChatConversation
import org.example.project.domain.model.chat.Message

interface ChatRepository {
    // Chat List
    suspend fun getConversations(): Result<List<ChatConversation>> // For Patient viewing Doctors
    suspend fun getPatientConversations(): Result<List<ChatConversation>> // For Doctor viewing Patients

    // Chat Details
    fun getMessages(recipientId: String): Flow<List<Message>>
    suspend fun sendMessage(recipientId: String, text: String): Result<Message>
    fun observeDoctorTyping(recipientId: String): Flow<Boolean>
}
