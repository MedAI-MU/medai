package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.example.project.data.remote.dto.SetPasswordRequest
import org.example.project.domain.repository.SetPasswordRepository

class NetworkSetPasswordRepository(
    private val client: HttpClient
) : SetPasswordRepository {

    override suspend fun setPassword(password: String): Result<Unit> {
        return try {
            // Later "auth/set-password" with actual backend endpoint
            client.post("auth/set-password") {
                setBody(SetPasswordRequest(password))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
