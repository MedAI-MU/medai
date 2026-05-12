package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.setCookie
import org.example.project.data.remote.dto.auth.AuthRegisterRequestDto
import org.example.project.data.remote.dto.auth.AuthRegisterResponseDto
import org.example.project.data.remote.dto.auth.JwtPayloadDto
import org.example.project.data.remote.util.decodeBase64String
import org.example.project.domain.repository.SignUpRepository

class NetworkSignUpRepository(
    private val client: HttpClient
) : SignUpRepository {

    override suspend fun register(request: AuthRegisterRequestDto): Result<AuthRegisterResponseDto> {
        return try {
            // 1. Register User
            val registerResponse = client.post("users") {
                setBody(request)
            }

            if (registerResponse.status.value !in 200..299) {
                 throw Exception("Registration failed: ${registerResponse.status}")
            }

            // 2. Auto-Login to get tokens
            val loginResponse = client.post("auth/login") {
                setBody(mapOf("email" to request.email, "password" to request.password))
            }

            if (loginResponse.status.value !in 200..299) {
                 throw Exception("Auto-login failed: ${loginResponse.status}")
            }

            // 3. Extract JWT from Set-Cookie Header (Client-side decoding)
            val cookies = loginResponse.setCookie()
            val authToken = cookies.find { it.name == "Authentication" }?.value
                ?: throw Exception("Authentication cookie not found in response")

            println("SignUpRepository: Auth Token extracted successfully: ${authToken.take(10)}...")

            // 4. Decode JWT Payload
            val parts = authToken.split(".")
            if (parts.size < 2) throw Exception("Invalid JWT format")

            val payloadJson = parts[1].decodeBase64String()

            // 5. Deserialize
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            val claims = json.decodeFromString<JwtPayloadDto>(payloadJson)

            Result.success(AuthRegisterResponseDto(
                token = authToken,
                userId = claims.userId,
                role = claims.role ?: "patient",
                message = "User registered and logged in successfully"
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            println("SignUpRepository: Error during registration: ${e.message}")
            Result.failure(e)
        }
    }
}
