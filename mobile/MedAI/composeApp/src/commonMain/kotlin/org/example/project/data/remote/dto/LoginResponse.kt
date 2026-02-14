package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("sub") val sub: Int,
    val email: String,
    val role: String? = null,
) {
    val userId: String get() = sub.toString()
    val name: String get() = email.substringBefore("@")
}
