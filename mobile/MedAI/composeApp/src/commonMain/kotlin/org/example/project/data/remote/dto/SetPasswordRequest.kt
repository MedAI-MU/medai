package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SetPasswordRequest(
    val password: String
)
