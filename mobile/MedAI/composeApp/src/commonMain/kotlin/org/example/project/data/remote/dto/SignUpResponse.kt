package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val token: String,
    val userId: String,
    val message: String
)
