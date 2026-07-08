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
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NetworkAppointmentRepositoryTest {

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
    fun testGetAppointmentsSuccess() = runTest {
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "patientUserId": 10,
                    "doctorUserId": 20,
                    "scheduleSlotId": 5,
                    "status": "pending",
                    "createdAt": "2023-10-01T10:00:00"
                },
                {
                    "id": 2,
                    "patientUserId": 10,
                    "doctorUserId": 21,
                    "scheduleSlotId": 6,
                    "status": "finished",
                    "createdAt": "2023-10-02T10:00:00"
                }
            ]
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkAppointmentRepository(client)

        // Test UPCOMING status filter
        val upcomingResult = repository.getAppointments(AppointmentDetailStatus.UPCOMING)
        assertTrue(upcomingResult.isSuccess)
        assertEquals(1, upcomingResult.getOrNull()?.size)
        assertEquals("1", upcomingResult.getOrNull()?.first()?.id)

        // Test FINISHED status filter
        val finishedResult = repository.getAppointments(AppointmentDetailStatus.FINISHED)
        assertTrue(finishedResult.isSuccess)
        assertEquals(1, finishedResult.getOrNull()?.size)
        assertEquals("2", finishedResult.getOrNull()?.first()?.id)
    }

    @Test
    fun testGetMyAppointmentsSuccess() = runTest {
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "patientUserId": 10,
                    "doctorUserId": 20,
                    "scheduleSlotId": 5,
                    "status": "pending",
                    "createdAt": "2023-10-01T10:00:00"
                }
            ]
        """.trimIndent()

        val client = createMockClient(jsonResponse)
        val repository = NetworkAppointmentRepository(client)

        val result = repository.getMyAppointments()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("1", result.getOrNull()?.first()?.id)
    }

    @Test
    fun testCancelAppointmentSuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkAppointmentRepository(client)

        val result = repository.cancelAppointment("1")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testSubmitReviewSuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkAppointmentRepository(client)

        val result = repository.submitReview("1", 5, "Great")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testGetCancelReasonsSuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkAppointmentRepository(client)

        val result = repository.getCancelReasons()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isNotEmpty())
    }
}
