package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import org.example.project.data.remote.dto.auth.AuthResultDto
import org.example.project.domain.model.UserRole
import org.example.project.domain.repository.LoginRepository

class MockLoginRepository : LoginRepository {
    override suspend fun login(email: String, password: String): Result<AuthResultDto> {
        // Simulate network delay (2 seconds)
        delay(2000)

        return if (email.contains("error")) {
            // Simulate a backend error if specific text is entered
            Result.failure(Exception("Invalid credentials"))
        } else {
            // Simulate success
            Result.success(
                AuthResultDto(
                    userId = "patient_123_jane",
                    token = "mock_token_xyz",
                    userName = "Jane Doe",
                    role = "PATIENT",
                    email = "janedoe@example.com"
                )
            )
        }
    }
}
