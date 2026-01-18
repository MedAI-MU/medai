package org.example.project.domain.repository

import org.example.project.domain.model.User

interface ProfileRepository {
    suspend fun getUserProfile(): Result<User>
    suspend fun logout(): Result<Unit>
}
