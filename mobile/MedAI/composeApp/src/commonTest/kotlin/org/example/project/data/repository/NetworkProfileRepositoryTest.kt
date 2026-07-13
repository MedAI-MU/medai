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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.repository.auth.UserSessionManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import org.example.project.domain.model.auth.AccountStatus

class NetworkProfileRepositoryTest {

    private class MockUserSessionManager(
        private val loggedIn: Boolean,
        private val userId: String? = null,
        private val userName: String? = null,
        private val email: String? = null,
        private val role: UserRole? = null
    ) : UserSessionManager {
        override val isUserLoggedIn: Flow<Boolean> = flowOf(loggedIn)
        override suspend fun getUserId() = userId
        override suspend fun getUserName() = userName
        override suspend fun getUserEmail() = email
        override suspend fun getUserToken() = "mock_token"
        override suspend fun getUserRole() = role
        override suspend fun getAccountStatus(): AccountStatus? = null
        override suspend fun updateAccountStatus(status: AccountStatus) {}
        override suspend fun getCookies() = emptySet<String>()
        override suspend fun saveCookies(cookies: Set<String>) {}
        override suspend fun updateUserToken(token: String) {}
        override suspend fun saveSession(
            userId: String,
            token: String,
            name: String,
            email: String,
            role: UserRole,
            accountStatus: AccountStatus
        ) {}
        override suspend fun clearSession() {}
    }

    private fun createMockClient(
        responseBody: String = """
            {
                "id": 1,
                "name": "Jane Doe",
                "email": "jane@example.com",
                "phone": "1234567890",
                "role": "doctor",
                "status": "approved"
            }
        """.trimIndent(),
        status: HttpStatusCode = HttpStatusCode.OK
    ): HttpClient {
        val mockEngine = MockEngine { request ->
            respond(
                content = responseBody,
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
    fun testGetUserProfileSuccess() = runTest {
        val sessionManager = MockUserSessionManager(
            loggedIn = true,
            userId = "1",
            userName = "Jane Doe",
            email = "jane@example.com",
            role = UserRole.DOCTOR
        )
        val client = createMockClient()
        val repository = NetworkProfileRepository(client, sessionManager)
        val result = repository.getUserProfile()

        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("1", user.id)
        assertEquals("Jane Doe", user.name)
        assertEquals("jane@example.com", user.email)
    }

    @Test
    fun testGetUserProfileFailureWhenNotLoggedIn() = runTest {
        val sessionManager = MockUserSessionManager(loggedIn = false)
        val client = createMockClient()
        val repository = NetworkProfileRepository(client, sessionManager)

        val result = repository.getUserProfile()
        assertTrue(result.isFailure)
    }
}
