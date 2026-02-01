package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.ChatConversation
import org.example.project.domain.model.Message

interface ChatRepository {
    // Chat List
    suspend fun getConversations(): Result<List<ChatConversation>>

    // Chat Details
    fun getMessages(doctorId: String): Flow<List<Message>>
    suspend fun sendMessage(doctorId: String, text: String): Result<Message>
    fun observeDoctorTyping(doctorId: String): Flow<Boolean>
}
