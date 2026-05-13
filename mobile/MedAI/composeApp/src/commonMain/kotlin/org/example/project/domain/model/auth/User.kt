package org.example.project.domain.model.auth

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null
)
