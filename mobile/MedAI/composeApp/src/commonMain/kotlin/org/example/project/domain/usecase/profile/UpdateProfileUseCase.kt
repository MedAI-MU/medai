package org.example.project.domain.usecase.profile

import org.example.project.domain.model.auth.User
import org.example.project.domain.repository.profile.ProfileRepository

class UpdateProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        userId: String,
        name: String?,
        phone: String?,
        birthDate: String?,
        gender: String?,
        bio: String?,
        about: String? = null
    ): Result<User> {
        return repository.updateUserProfile(
            userId = userId,
            name = name,
            phone = phone,
            birthDate = birthDate,
            gender = gender,
            bio = bio,
            about = about
        )
    }
}
