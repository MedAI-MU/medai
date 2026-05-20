package org.example.project.domain.model.chat

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Message(
    val id: String,
    val text: String,
    val senderId: String,
    val timestamp: Instant = Clock.System.now(),
    val status: MessageStatus = MessageStatus.SENT,
    val isFromUser: Boolean
)

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ, FAILED
}
