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

class NetworkHomeRepositoryTest {

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
    fun testGetCategoriesSuccess() = runTest {
        val client = createMockClient("{}") // Not used as it's hardcoded for now
        val repository = NetworkHomeRepository(client)

        val result = repository.getCategories()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isNotEmpty())
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
        val repository = NetworkHomeRepository(client)

        val result = repository.getSpecialties()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isNotEmpty())
    }

    @Test
    fun testGetUpcomingAppointmentsSuccess() = runTest {
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "patientUserId": 1,
                    "doctorUserId": 2,
                    "scheduleSlotId": 5,
                    "status": "pending",
                    "createdAt": "2023-10-01T10:00:00"
                }
            ]
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkHomeRepository(client)

        val result = repository.getUpcomingAppointments()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("1", result.getOrNull()?.first()?.id)
    }
}
