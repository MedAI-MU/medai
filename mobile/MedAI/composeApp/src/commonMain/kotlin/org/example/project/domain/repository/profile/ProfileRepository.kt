package org.example.project.domain.repository.profile

import org.example.project.domain.model.auth.User

interface ProfileRepository {
    suspend fun getUserProfile(): Result<User>

    suspend fun updateUserProfile(
        userId: String,
        name: String?,
        phone: String?,
        birthDate: String?,
        gender: String?,
        bio: String?,
        about: String? = null
    ): Result<User>

    suspend fun uploadAvatar(
        userId: String,
        imageBytes: ByteArray,
        fileName: String
    ): Result<String>

    suspend fun logout(): Result<Unit>
}
