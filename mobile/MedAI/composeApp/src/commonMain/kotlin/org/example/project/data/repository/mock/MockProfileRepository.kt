package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import org.example.project.domain.model.auth.User
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.repository.profile.ProfileRepository

class MockProfileRepository : ProfileRepository {
    override suspend fun getUserProfile(): Result<User> {
        delay(500)
        return Result.success(
            User(
                id = "u1",
                name = "Jane Doe",
                email = "janedoe@example.com",
                role = UserRole.PATIENT
            )
        )
    }

    override suspend fun logout(): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }
}
