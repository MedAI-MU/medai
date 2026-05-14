package org.example.project.domain.model.auth

data class AuthResult(
    val userId: String,
    val token: String,
    val userName: String = "",
    val email: String = "",
    val role: String
)
