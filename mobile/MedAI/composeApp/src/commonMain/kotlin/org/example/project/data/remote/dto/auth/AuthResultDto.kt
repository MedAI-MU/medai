package org.example.project.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * Mobile-internal DTO representing the result of a successful authentication flow.
 * Not a direct backend response — assembled from login response + cookie extraction.
 */
@Serializable
data class AuthResultDto(
    val userId: String,
    val token: String,
    val userName: String,
    val role: String,
    val email: String
)
