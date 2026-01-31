package org.example.project.data.remote.dto
import kotlinx.serialization.Serializable


@Serializable
data class SignUpRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val mobile: String,
    val dob: String,
    val role: String
)
