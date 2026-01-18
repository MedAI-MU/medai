package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import org.example.project.data.remote.dto.UserDto
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.User
import org.example.project.domain.repository.ProfileRepository

class MockProfileRepository : ProfileRepository {
    override suspend fun getUserProfile(): Result<User> {
        delay(500)
        return Result.success(
            UserDto(
                id = "u1",
                name = "Jane Doe",
                email = "janedoe@example.com",
                role = "patient"
            ).toDomain()
        )
    }

    override suspend fun logout(): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }
}
