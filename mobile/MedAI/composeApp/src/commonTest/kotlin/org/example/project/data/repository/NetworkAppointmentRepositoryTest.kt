package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.model.auth.AccountStatus
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.model.patient.*
import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.repository.patient.PatientRepository
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

    private val fakePatientRepository = FakePatientRepository()
    private val fakeUserSessionManager = FakeUserSessionManager()

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
        val repository = NetworkAppointmentRepository(client, fakePatientRepository, fakeUserSessionManager)

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
        val repository = NetworkAppointmentRepository(client, fakePatientRepository, fakeUserSessionManager)

        val result = repository.getMyAppointments()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("1", result.getOrNull()?.first()?.id)
    }

    @Test
    fun testCancelAppointmentSuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkAppointmentRepository(client, fakePatientRepository, fakeUserSessionManager)

        val result = repository.cancelAppointment("1")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testSubmitReviewSuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkAppointmentRepository(client, fakePatientRepository, fakeUserSessionManager)

        val result = repository.submitReview("1", 5, "Great")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testGetCancelReasonsSuccess() = runTest {
        val client = createMockClient("{}")
        val repository = NetworkAppointmentRepository(client, fakePatientRepository, fakeUserSessionManager)

        val result = repository.getCancelReasons()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isNotEmpty())
    }
}

class FakePatientRepository : PatientRepository {
    override suspend fun getPatients(): Result<List<Patient>> = Result.success(emptyList())
    override suspend fun getPatientById(id: String): Result<Patient> = Result.success(
        Patient(id, "Patient $id", Gender.Male, 30, "12345", 70.0, 175.0, BloodType.O_POS, MaritalStatus.Single)
    )
    override suspend fun createPatient(patient: CreatePatientParams): Result<Boolean> = Result.success(true)
    override suspend fun updatePatient(id: String, patient: UpdatePatientParams): Result<Boolean> = Result.success(true)
    override suspend fun addAllergy(patientId: String, allergy: AllergyParams): Result<Boolean> = Result.success(true)
    override suspend fun updateAllergy(patientId: String, allergyId: String, allergy: AllergyParams): Result<Boolean> = Result.success(true)
    override suspend fun deleteAllergy(patientId: String, allergyId: String): Result<Boolean> = Result.success(true)
    override suspend fun addChronicDisease(patientId: String, disease: ChronicDiseaseParams): Result<Boolean> = Result.success(true)
    override suspend fun updateChronicDisease(patientId: String, diseaseId: String, disease: ChronicDiseaseParams): Result<Boolean> = Result.success(true)
    override suspend fun deleteChronicDisease(patientId: String, diseaseId: String): Result<Boolean> = Result.success(true)
    override suspend fun addFamilyHistory(patientId: String, history: FamilyHistoryParams): Result<Boolean> = Result.success(true)
    override suspend fun updateFamilyHistory(patientId: String, historyId: String, history: FamilyHistoryParams): Result<Boolean> = Result.success(true)
    override suspend fun deleteFamilyHistory(patientId: String, historyId: String): Result<Boolean> = Result.success(true)
    override suspend fun addSurgery(patientId: String, surgery: SurgeryParams): Result<Boolean> = Result.success(true)
    override suspend fun updateSurgery(patientId: String, surgeryId: String, surgery: SurgeryParams): Result<Boolean> = Result.success(true)
    override suspend fun deleteSurgery(patientId: String, surgeryId: String): Result<Boolean> = Result.success(true)
    override suspend fun addEmergencyContact(patientId: String, contact: EmergencyContactParams): Result<Boolean> = Result.success(true)
    override suspend fun updateEmergencyContact(patientId: String, contactId: String, contact: EmergencyContactParams): Result<Boolean> = Result.success(true)
    override suspend fun deleteEmergencyContact(patientId: String, contactId: String): Result<Boolean> = Result.success(true)
}

class FakeUserSessionManager : UserSessionManager {
    override suspend fun getUserId(): String? = "10"
    override suspend fun getUserName(): String? = "John Doe"
    override suspend fun getUserEmail(): String? = "john@example.com"
    override suspend fun getUserToken(): String? = "token"
    override suspend fun getUserRole(): UserRole? = UserRole.PATIENT
    override suspend fun getAccountStatus(): AccountStatus? = AccountStatus.APPROVED
    override suspend fun updateAccountStatus(status: AccountStatus) {}
    override suspend fun saveSession(userId: String, token: String, name: String, email: String, role: UserRole, accountStatus: AccountStatus) {}
    override suspend fun getCookies(): Set<String> = emptySet()
    override suspend fun saveCookies(cookies: Set<String>) {}
    override suspend fun clearSession() {}
    override val isUserLoggedIn: Flow<Boolean> = emptyFlow()
}
