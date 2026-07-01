package org.example.project.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.repository.auth.UserSessionManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        override suspend fun getUserToken() = null
        override suspend fun getUserRole() = role
        override suspend fun getCookies() = emptySet<String>()
        override suspend fun saveCookies(cookies: Set<String>) {}
        override suspend fun saveSession(userId: String, token: String, name: String, email: String, role: UserRole) {}
        override suspend fun clearSession() {}
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
        val repository = NetworkProfileRepository(sessionManager)
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
        val repository = NetworkProfileRepository(sessionManager)

        val result = repository.getUserProfile()
        assertTrue(result.isFailure)
    }
}
