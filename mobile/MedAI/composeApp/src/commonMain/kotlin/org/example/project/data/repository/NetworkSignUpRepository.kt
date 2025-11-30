package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.example.project.data.remote.dto.SignUpRequest
import org.example.project.data.remote.dto.SignUpResponse
import org.example.project.domain.repository.SignUpRepository

class NetworkSignUpRepository(
    private val client: HttpClient
) : SignUpRepository {

    override suspend fun register(request: SignUpRequest): Result<SignUpResponse> {
        return try {
            val response: SignUpResponse = client.post("auth/register") {
                setBody(request)
            }.body()


            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
