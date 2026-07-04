package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.setCookie
import org.example.project.data.remote.dto.auth.AuthRegisterRequestDto
import org.example.project.data.remote.dto.auth.JwtPayloadDto
import org.example.project.data.remote.util.decodeBase64String
import org.example.project.domain.repository.auth.SignUpRepository
import org.example.project.domain.model.auth.AuthResult
import org.example.project.domain.model.auth.RegisterRequest

import io.ktor.http.contentType
import org.example.project.data.remote.mapper.mapAccountStatus

class NetworkSignUpRepository(
    private val client: HttpClient
) : SignUpRepository {

    override suspend fun register(request: RegisterRequest): Result<AuthResult> {
        return try {
            val requestDto = AuthRegisterRequestDto(
                name = request.name,
                email = request.email,
                password = request.password,
                phone = request.phone,
                role = request.role
            )
            // 1. Register User
            val registerResponse = client.post("users") {
                contentType(ContentType.Application.Json)
                setBody(requestDto)
            }

            if (registerResponse.status.value !in 200..299) {
                 throw Exception("Registration failed: ${registerResponse.status}")
            }

            // 2. Auto-Login to get tokens
            val loginResponse = client.post("auth/login") {
                contentType(ContentType.Application.Json)
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

            Result.success(AuthResult(
                userId = claims.userId,
                token = authToken,
                userName = request.name,
                email = request.email,
                role = claims.role ?: "patient",
                accountStatus = mapAccountStatus(claims.status)
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            println("SignUpRepository: Error during registration: ${e.message}")
            Result.failure(e)
        }
    }
}
