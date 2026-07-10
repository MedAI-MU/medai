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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NetworkSpecialtiesRepositoryTest {

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
    fun testGetSpecialtiesSuccess() = runTest {
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "name": "Cardiology"
                }
            ]
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkSpecialtiesRepository(client)

        val result = repository.getSpecialties()
        assertTrue(result.isSuccess)
        val specialties = result.getOrNull()!!
        assertEquals(1, specialties.size)
        assertEquals("Cardiology", specialties[0].id)
        assertEquals("Cardiology", specialties[0].iconName)
    }
}
