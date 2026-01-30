package org.example.project.domain.model

data class ChatConversation(
    val doctorId: String,
    val doctorName: String,
    val doctorImageUrl: String?,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false
)
