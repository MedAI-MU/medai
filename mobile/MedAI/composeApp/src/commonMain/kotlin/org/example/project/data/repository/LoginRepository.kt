package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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
            val response: LoginResponse = httpClient.post("auth/login") {
                setBody(LoginRequest(email, password))
            }.body()

            // TODO: Save token securely (e.g., to KeyStore/Keychain via Multiplatform Settings)
            // SecureStorage.saveToken(response.token)

            Result.success(AuthResult(response.userId, response.token, response.name, response.role ?: "patient"))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
