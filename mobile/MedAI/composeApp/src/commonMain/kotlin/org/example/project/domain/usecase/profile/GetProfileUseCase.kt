package org.example.project.domain.usecase.profile

import org.example.project.domain.model.auth.User
import org.example.project.domain.repository.profile.ProfileRepository

class GetProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<User> {
        return repository.getUserProfile()
    }
}
