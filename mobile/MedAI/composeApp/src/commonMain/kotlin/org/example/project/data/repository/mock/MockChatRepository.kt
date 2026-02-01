package org.example.project.data.repository.mock


import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import org.example.project.domain.model.ChatConversation
import org.example.project.domain.model.Message
import org.example.project.domain.model.MessageStatus
import org.example.project.domain.repository.ChatRepository
import kotlin.time.Duration.Companion.seconds

class MockChatRepository : ChatRepository {

    private val _messages = MutableStateFlow<List<Message>>(
        listOf(
            Message("1", "Hello Dr. Smith, question about my prescription.", "me", Clock.System.now().minus(10.seconds), MessageStatus.READ, true),
            Message("2", "Hi! Sure, ask away.", "doc1", Clock.System.now().minus(5.seconds), MessageStatus.READ, false)
        )
    )

    override suspend fun getConversations(): Result<List<ChatConversation>> {
        delay(500)
        return Result.success(
            listOf(
                ChatConversation("d1", "Dr. Olivia Turner", null, "Take medicine twice a day.", "10:30 AM", 2, true),
                ChatConversation("d2", "Dr. Alexander Bennett", null, "Appointment confirmed.", "Yesterday", 0, false)
            )
        )
    }

    override fun getMessages(doctorId: String): Flow<List<Message>> {
        return _messages.map { list -> list.sortedByDescending { it.timestamp } }
    }

    override suspend fun sendMessage(doctorId: String, text: String): Result<Message> {
        val newMessage = Message(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            text = text,
            senderId = "me",
            status = MessageStatus.SENT,
            isFromUser = true
        )
        val current = _messages.value.toMutableList()
        current.add(0, newMessage)
        _messages.value = current

        delay(1000) // Simulate Auto-Reply
        val reply = Message(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            text = "I received your message: \"$text\"",
            senderId = doctorId,
            status = MessageStatus.READ,
            isFromUser = false
        )
        _messages.value = _messages.value + reply

        return Result.success(newMessage)
    }

    override fun observeDoctorTyping(doctorId: String): Flow<Boolean> = flow {
        while(true) {
            emit(false)
            delay(5000)
            emit(true)
            delay(3000)
        }
    }
}
