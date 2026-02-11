package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.setCookie
import org.example.project.data.remote.util.decodeBase64String
import org.example.project.data.remote.dto.AuthResult
import org.example.project.data.remote.dto.LoginRequest
import org.example.project.data.remote.dto.LoginResponse
import org.example.project.domain.repository.LoginRepository

class NetworkLoginRepository(
    private val httpClient: HttpClient
) : LoginRepository {
    override suspend fun login(email: String, password: String): Result<AuthResult> {
        return try {
            // Example call - secure and serialized
            // 1. Perform Login
            val loginResponse = httpClient.post("auth/login") {
                setBody(LoginRequest(email, password))
            }

            if (loginResponse.status.value !in 200..299) {
                 throw Exception("Login failed: ${loginResponse.status}")
            }

            // 2. Extract JWT from Set-Cookie Header
            val setCookieHeader = loginResponse.headers["Set-Cookie"]
                ?: throw Exception("No Set-Cookie header found")

            // Simple parsing to find "Authentication=..."
            val authToken = setCookieHeader.split(";")
                .find { it.trim().startsWith("Authentication=") }
                ?.substringAfter("Authentication=")
                ?.substringBefore(";")
                ?: throw Exception("Authentication cookie not found")

            // 4. Decode JWT Payload
            val parts = authToken.split(".")
            if (parts.size < 2) throw Exception("Invalid JWT format")

            val payloadJson = parts[1].decodeBase64String()

            // 5. Deserialize
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            val claims = json.decodeFromString<LoginResponse>(payloadJson)

            Result.success(AuthResult(claims.userId, authToken, claims.name, claims.role ?: "patient"))
        } catch (e: Exception) {
            e.printStackTrace()
            println("LoginRepository: Error during login: ${e.message}")
            if(e is kotlinx.serialization.SerializationException) {
                println("LoginRepository: Serialization error. Check DTO vs JSON.")
            }
            Result.failure(e)
        }
    }
}
