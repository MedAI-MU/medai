package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import org.example.project.domain.model.auth.User
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.repository.profile.ProfileRepository

class MockProfileRepository : ProfileRepository {
    private var mockUser = User(
        id = "1",
        name = "Jane Doe",
        email = "janedoe@example.com",
        role = UserRole.PATIENT,
        phoneNumber = "01123456789",
        avatarUrl = null,
        bio = "Hello, I am Jane Doe!",
        about = null,
        gender = "female",
        birthDate = "1995-05-15"
    )

    override suspend fun getUserProfile(): Result<User> {
        delay(500)
        return Result.success(mockUser)
    }

    override suspend fun updateUserProfile(
        userId: String,
        name: String?,
        phone: String?,
        birthDate: String?,
        gender: String?,
        bio: String?
    ): Result<User> {
        delay(500)
        mockUser = mockUser.copy(
            name = name ?: mockUser.name,
            phoneNumber = phone ?: mockUser.phoneNumber,
            birthDate = birthDate ?: mockUser.birthDate,
            gender = gender ?: mockUser.gender,
            bio = bio ?: mockUser.bio
        )
        return Result.success(mockUser)
    }

    override suspend fun uploadAvatar(
        userId: String,
        imageBytes: ByteArray,
        fileName: String
    ): Result<String> {
        delay(500)
        val mockUrl = "https://example.com/avatar/$fileName"
        mockUser = mockUser.copy(avatarUrl = mockUrl)
        return Result.success(mockUrl)
    }

    override suspend fun logout(): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }
}
