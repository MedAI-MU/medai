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
import org.example.project.domain.model.patient.AllergyParams
import org.example.project.domain.model.patient.CreatePatientParams
import kotlin.test.Test
import kotlin.test.assertTrue

class NetworkPatientRepositoryTest {

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
    fun testGetPatientsSuccess() = runTest {
        // Mock empty list to avoid complex DTO mapping
        val client = createMockClient("[]")
        val repository = NetworkPatientRepository(client)

        val result = repository.getPatients()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun testCreatePatientSuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkPatientRepository(client)

        val params = CreatePatientParams(
            birthDate = "2000-01-01",
            gender = org.example.project.domain.model.patient.Gender.Male,
            bloodType = org.example.project.domain.model.patient.BloodType.O_POS
        )
        val result = repository.createPatient(params)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!)
    }

    @Test
    fun testAddAllergySuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkPatientRepository(client)

        val allergyParams = AllergyParams(
            name = "Peanuts",
            symptoms = "Anaphylaxis"
        )
        val result = repository.addAllergy("1", allergyParams)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!)
    }

    @Test
    fun testDeleteAllergySuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkPatientRepository(client)

        val result = repository.deleteAllergy("1", "1")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!)
    }
}
