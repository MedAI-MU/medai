package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    val title: String,
    val body: String,
    val timestamp: String, // "2025-01-05T10:00:00Z"
    val type: String,      // "appointment_confirmed"
    @SerialName("is_read") val isRead: Boolean
)
