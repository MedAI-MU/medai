package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.example.project.domain.model.auth.RegisterRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import io.ktor.client.plugins.defaultRequest

class NetworkSignUpRepositoryTest {

    private fun createMockClient(status: HttpStatusCode = HttpStatusCode.OK): HttpClient {
        val mockEngine = MockEngine { request ->
            if (request.url.encodedPath == "/users") {
                respond(
                    content = "{}",
                    status = status,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            } else if (request.url.encodedPath == "/auth/login") {
                respond(
                    content = "{}",
                    status = status,
                    headers = headersOf(
                        HttpHeaders.ContentType to listOf("application/json"),
                        HttpHeaders.SetCookie to listOf("Authentication=header.eyJzdWIiOiAxLCAiZW1haWwiOiAiam9obkBleGFtcGxlLmNvbSIsICJyb2xlIjogInBhdGllbnQifQ==.signature; Path=/; HttpOnly")
                    )
                )
            } else {
                respond("{}", HttpStatusCode.NotFound)
            }
        }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest {
                url("http://localhost/")
            }
        }
    }

    @Test
    fun testRegisterSuccess() = runTest {
        val client = createMockClient()
        val repository = NetworkSignUpRepository(client)

        val request = RegisterRequest(
            name = "Jane Doe",
            email = "jane@example.com",
            password = "password",
            phone = "1234567890",
            role = "patient"
        )
        val result = repository.register(request)
        assertTrue(result.isSuccess)
        val authResult = result.getOrNull()!!
        assertEquals("1", authResult.userId)
        assertEquals("Jane Doe", authResult.userName)
        assertEquals("jane@example.com", authResult.email)
        assertEquals("patient", authResult.role)
    }
}
