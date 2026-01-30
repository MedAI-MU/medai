package org.example.project.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null
)
