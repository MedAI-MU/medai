package org.example.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("sub") val sub: Int,
    val email: String,
    val role: String? = null,
    // Add other JWT fields if needed, e.g., iat, exp
) {
    val userId: String get() = sub.toString()
    val name: String get() = email.substringBefore("@") // Fallback since name is not in JWT
}
