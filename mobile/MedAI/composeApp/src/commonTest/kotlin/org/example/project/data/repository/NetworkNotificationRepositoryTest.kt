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
import kotlin.test.Test
import kotlin.test.assertTrue

class NetworkNotificationRepositoryTest {

    private fun createMockClient(responseContent: String, status: HttpStatusCode = HttpStatusCode.OK): HttpClient {
        val mockEngine = MockEngine { request ->
            respond(
                content = responseContent,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Test
    fun testGetNotificationsSuccess() = runTest {
        val client = createMockClient("[]")
        val repository = NetworkNotificationRepository(client)

        val result = repository.getNotifications()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun testMarkAsReadSuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkNotificationRepository(client)

        val result = repository.markAsRead("1")
        assertTrue(result.isSuccess)
    }
}
