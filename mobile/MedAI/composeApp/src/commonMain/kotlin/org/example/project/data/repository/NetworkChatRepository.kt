package org.example.project.data.repository


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.example.project.data.remote.dto.ChatConversationDto
import org.example.project.data.remote.dto.MessageDto
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.chat.ChatConversation
import org.example.project.domain.model.chat.Message
import org.example.project.domain.repository.chat.ChatRepository

class NetworkChatRepository(
    private val client: HttpClient
) : ChatRepository {

    // --- 1. Get Chat List ---
    override suspend fun getConversations(): Result<List<ChatConversation>> {
        return try {
            // GET /chats
            val dtos: List<ChatConversationDto> = client.get("/chats").body()

            // Map DTO -> Domain using the mapper we created
            val domainList = dtos.map { it.toDomain() }

            Result.success(domainList)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getPatientConversations(): Result<List<ChatConversation>> {
        TODO("Not yet implemented")
    }

    // --- 2. Get Messages (Polling) ---
    override fun getMessages(doctorId: String): Flow<List<Message>> = flow {
        while (true) {
            try {
                // GET /chats/{id}/messages
                val dtos: List<MessageDto> = client.get("/chats/$doctorId/messages").body()

                // TODO: Get real current user ID from UserSession/Settings
                val currentUserId = "me"

                val domainMessages = dtos.map { it.toDomain(currentUserId) }
                emit(domainMessages)
            } catch (e: Exception) {
                e.printStackTrace()
                // Emit empty list or keep previous state on error
            }
            // Poll every 3 seconds
            delay(3000)
        }
    }

    // --- 3. Send Message ---
    override suspend fun sendMessage(doctorId: String, text: String): Result<Message> {
        return try {
            // POST /chats/{id}/messages
            val requestBody = mapOf("text" to text)

            val responseDto: MessageDto = client.post("/chats/$doctorId/messages") {
                setBody(requestBody)
            }.body()

            val currentUserId = "me"
            Result.success(responseDto.toDomain(currentUserId))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // --- 4. Typing Indicator (Stub) ---
    override fun observeDoctorTyping(doctorId: String): Flow<Boolean> = flow {
        // Real implementation would use WebSockets here
        emit(false)
    }
}
