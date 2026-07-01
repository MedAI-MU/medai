package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import io.ktor.client.plugins.defaultRequest

class NetworkChatRepositoryTest {

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
            defaultRequest {
                url("http://localhost/")
            }
        }
    }

    @Test
    fun testGetConversationsSuccess() = runTest {
        val client = createMockClient("[]")
        val repository = NetworkChatRepository(client)

        val result = repository.getConversations()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun testSendMessageSuccess() = runTest {
        val jsonResponse = """
            {
                "id": "1",
                "text": "Hello",
                "sender_id": "1",
                "timestamp": "2023-01-01T12:00:00",
                "status": "sent"
            }
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkChatRepository(client)

        val result = repository.sendMessage("1", "Hello")
        assertTrue(result.isSuccess)
        assertEquals("Hello", result.getOrNull()?.text)
    }

    @Test
    fun testGetMessagesFlow() = runTest {
        val jsonResponse = """
            [
                {
                    "id": "1",
                    "text": "Hello",
                    "sender_id": "1",
                    "timestamp": "2023-01-01T12:00:00",
                    "status": "sent"
                }
            ]
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkChatRepository(client)

        val flow = repository.getMessages("1")
        val messages = flow.first()
        assertTrue(messages.isNotEmpty())
        assertEquals("Hello", messages.first().text)
    }
}
