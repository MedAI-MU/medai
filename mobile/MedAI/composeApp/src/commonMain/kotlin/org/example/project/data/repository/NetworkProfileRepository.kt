package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import org.example.project.data.remote.dto.UserDto
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.User
import org.example.project.domain.repository.ProfileRepository

class NetworkProfileRepository(
    private val client: HttpClient
) : ProfileRepository {

    override suspend fun getUserProfile(): Result<User> {
        return try {
            // 1. Fetch DTO from API
            val dto: UserDto = client.get("/user/profile").body()

            // 2. Map DTO to Domain Model
            val user = dto.toDomain()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val response = client.post("/auth/logout")
            if (response.status == HttpStatusCode.OK) {
                // Clear local session/tokens here if needed
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
