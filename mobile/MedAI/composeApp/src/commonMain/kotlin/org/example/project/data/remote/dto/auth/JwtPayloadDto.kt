package org.example.project.data.remote.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the decoded JWT payload from the Authentication cookie.
 * Used during sign-up flow to extract user info from the JWT after auto-login.
 */
@Serializable
data class JwtPayloadDto(
    @SerialName("sub") val sub: Int,
    val email: String,
    val role: String? = null,
) {
    val userId: String get() = sub.toString()
    val name: String get() = email.substringBefore("@")
}
