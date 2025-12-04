package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import org.example.project.domain.repository.LoginRepository

class MockLoginRepository : LoginRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {
        // Simulate network delay (2 seconds)
        delay(2000)

        return if (email.contains("error")) {
            // Simulate a backend error if specific text is entered
            Result.failure(Exception("Invalid credentials"))
        } else {
            // Simulate success
            Result.success(Unit)
        }
    }
}
