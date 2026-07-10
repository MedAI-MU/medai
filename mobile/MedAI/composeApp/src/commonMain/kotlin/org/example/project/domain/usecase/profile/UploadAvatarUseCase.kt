package org.example.project.domain.usecase.profile

import org.example.project.domain.repository.profile.ProfileRepository

class UploadAvatarUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        userId: String,
        imageBytes: ByteArray,
        fileName: String
    ): Result<String> {
        return repository.uploadAvatar(
            userId = userId,
            imageBytes = imageBytes,
            fileName = fileName
        )
    }
}
