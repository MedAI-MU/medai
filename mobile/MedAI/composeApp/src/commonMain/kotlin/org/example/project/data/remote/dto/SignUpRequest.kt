package org.example.project.data.remote.dto
import kotlinx.serialization.Serializable


@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val role: String
)
