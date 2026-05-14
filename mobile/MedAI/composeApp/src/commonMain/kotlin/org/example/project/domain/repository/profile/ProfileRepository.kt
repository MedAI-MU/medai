package org.example.project.domain.repository.profile

import org.example.project.domain.model.auth.User

interface ProfileRepository {
    suspend fun getUserProfile(): Result<User>
    suspend fun logout(): Result<Unit>
}
