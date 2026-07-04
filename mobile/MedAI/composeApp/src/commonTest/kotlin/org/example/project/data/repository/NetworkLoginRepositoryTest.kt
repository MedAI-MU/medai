package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NetworkLoginRepositoryTest {

    private fun createMockClient(status: HttpStatusCode = HttpStatusCode.OK): HttpClient {
        val mockEngine = MockEngine { request ->
            respond(
                content = """
                    {
                        "id": 1,
                        "name": "John Doe",
                        "email": "john@example.com",
                        "role": "patient"
                    }
                """.trimIndent(),
                status = status,
                headers = headersOf(
                    HttpHeaders.ContentType to listOf("application/json"),
                    HttpHeaders.SetCookie to listOf("Authentication=mock_jwt_token; Path=/; HttpOnly")
                )
            )
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
    fun testLoginSuccess() = runTest {
        val client = createMockClient()
        val repository = NetworkLoginRepository(client)

        val result = repository.login("john@example.com", "password")
        assertTrue(result.isSuccess)

        val authResult = result.getOrNull()!!
        assertEquals("1", authResult.userId)
        assertEquals("mock_jwt_token", authResult.token)
        assertEquals("John Doe", authResult.userName)
        assertEquals("john@example.com", authResult.email)
        assertEquals("patient", authResult.role)
    }

    @Test
    fun testLoginFailureMissingCookie() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = "{}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest {
                url("http://localhost/")
            }
        }
        val repository = NetworkLoginRepository(client)

        val result = repository.login("john@example.com", "password")
        assertTrue(result.isFailure)
        assertEquals("Authentication cookie not found in response", result.exceptionOrNull()?.message)
    }

    @Test
    fun testLoginFailureServerError() = runTest {
        val client = createMockClient(status = HttpStatusCode.InternalServerError)
        val repository = NetworkLoginRepository(client)

        val result = repository.login("john@example.com", "password")
        assertTrue(result.isFailure)
    }
}
