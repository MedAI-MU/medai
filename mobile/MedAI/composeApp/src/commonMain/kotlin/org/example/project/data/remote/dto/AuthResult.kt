package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResult(val userId: String, val token: String, val userName: String, val role: String, val email: String)
