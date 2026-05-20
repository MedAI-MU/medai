package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.setCookie
import org.example.project.data.remote.dto.auth.AuthLoginRequestDto
import org.example.project.data.remote.dto.auth.AuthLoginResponseDto
import org.example.project.domain.model.auth.AuthResult
import org.example.project.domain.repository.auth.LoginRepository
import io.ktor.client.call.body

class NetworkLoginRepository(
    private val httpClient: HttpClient
) : LoginRepository {
    override suspend fun login(email: String, password: String): Result<AuthResult> {
        return try {
            // Example call - secure and serialized
            // 1. Perform Login
            val loginResponse = httpClient.post("auth/login") {
                setBody(AuthLoginRequestDto(email, password))
            }

            if (loginResponse.status.value !in 200..299) {
                 throw Exception("Login failed: ${loginResponse.status}")
            }

            // 2. Extract JWT from Set-Cookie Header (using Ktor's cookie extension)
            val cookies = loginResponse.setCookie()
            val authToken = cookies.find { it.name == "Authentication" }?.value
                ?: throw Exception("Authentication cookie not found in response")

            // 3. Extract the actual body
            val responseBody = loginResponse.body<AuthLoginResponseDto>()

            Result.success(AuthResult(responseBody.id.toString(), authToken, responseBody.name, responseBody.email ?: "", responseBody.role ?: "patient"))
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
