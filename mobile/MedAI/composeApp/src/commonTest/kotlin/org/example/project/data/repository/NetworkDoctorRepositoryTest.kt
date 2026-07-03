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
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import io.ktor.client.plugins.defaultRequest

class NetworkDoctorRepositoryTest {

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
    fun testGetDoctorsSuccess() = runTest {
        val jsonResponse = """
            [
                {
                    "userId": 1,
                    "name": "John Doe",
                    "specialities": [
                        {
                            "id": 1,
                            "isPrimary": true,
                            "yearsOfExperience": 5,
                            "speciality": { "id": 1, "name": "Cardiology" }
                        }
                    ]
                }
            ]
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkDoctorRepository(client)

        val result = repository.getDoctors(null)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("1", result.getOrNull()?.first()?.id)
    }

    @Test
    fun testGetDoctorByIdSuccess() = runTest {
        val jsonResponse = """
            {
                "userId": 1,
                "name": "John Doe",
                "specialities": [
                    {
                        "id": 1,
                        "isPrimary": true,
                        "yearsOfExperience": 5,
                        "speciality": { "id": 1, "name": "Cardiology" }
                    }
                ]
            }
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkDoctorRepository(client)

        val result = repository.getDoctorById("1")
        assertTrue(result.isSuccess)
        assertEquals("1", result.getOrNull()?.id)
    }

    @Test
    fun testGetAvailableSlotsSuccess() = runTest {
        val jsonResponse = """
            {
                "doctorId": 1,
                "name": "Dr. Smith",
                "speciality": "Cardiology",
                "days": {
                    "data": [
                        {
                            "day": "2023-10-01",
                            "slots": [
                                { "id": 1, "startTime": "10:00", "endTime": "10:30", "status": "available" },
                                { "id": 2, "startTime": "10:30", "endTime": "11:00", "status": "booked" }
                            ]
                        }
                    ],
                    "totalCount": 1,
                    "currentPage": 1,
                    "pageSize": 10,
                    "hasNext": false,
                    "hasPrevious": false
                }
            }
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkDoctorRepository(client)

        val result = repository.getAvailableSlots("1", LocalDate(2023, 10, 1))
        assertTrue(result.isSuccess)
        val slots = result.getOrNull()!!
        assertEquals(2, slots.size)
        assertEquals("10:00", slots[0].time)
        assertTrue(slots[0].isAvailable)
        assertEquals("10:30", slots[1].time)
        assertTrue(!slots[1].isAvailable)
    }

    @Test
    fun testBookAppointmentSuccess() = runTest {
        val jsonResponse = """
            {
                "id": 100,
                "patientUserId": 1,
                "doctorUserId": 2,
                "scheduleSlotId": 5,
                "status": "pending",
                "createdAt": "2023-10-01T10:00:00"
            }
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkDoctorRepository(client)

        val result = repository.bookAppointment("2", "5")
        assertTrue(result.isSuccess)
        assertEquals("100", result.getOrNull())
    }
}
