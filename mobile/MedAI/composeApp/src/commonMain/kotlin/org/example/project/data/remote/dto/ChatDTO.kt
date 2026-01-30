package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatConversationDto(
    @SerialName("doctor_id") val doctorId: String,
    @SerialName("doctor_name") val doctorName: String,
    @SerialName("doctor_image") val doctorImage: String?,
    @SerialName("last_message") val lastMessage: String,
    @SerialName("last_message_time") val lastMessageTime: String,
    @SerialName("unread_count") val unreadCount: Int,
    @SerialName("is_online") val isOnline: Boolean
)

@Serializable
data class MessageDto(
    val id: String,
    val text: String,
    @SerialName("sender_id") val senderId: String,
    val timestamp: String, // ISO 8601
    val status: String
)
