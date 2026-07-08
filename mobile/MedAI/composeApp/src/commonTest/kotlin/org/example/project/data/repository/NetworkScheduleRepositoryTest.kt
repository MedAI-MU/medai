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

class NetworkScheduleRepositoryTest {

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
    fun testGetScheduleTemplatesSuccess() = runTest {
        val jsonResponse = """
            {
                "data": [],
                "totalCount": 0,
                "currentPage": 1,
                "pageSize": 10,
                "hasNext": false,
                "hasPrevious": false
            }
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkScheduleRepository(client)

        val result = repository.getScheduleTemplates(1, 10, null, 1)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.data.isEmpty())
    }

    @Test
    fun testCreateScheduleTemplateSuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkScheduleRepository(client)

        val result = repository.createScheduleTemplate(1, "Test", emptyList())
        assertTrue(result.isSuccess)
    }
}
