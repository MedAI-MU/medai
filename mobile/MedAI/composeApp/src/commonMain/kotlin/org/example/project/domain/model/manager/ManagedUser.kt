package org.example.project.domain.model.manager

data class ManagedUser(
    val id: Int,
    val name: String,
    val role: String,
    val status: String // "pending" or "approved"
)
