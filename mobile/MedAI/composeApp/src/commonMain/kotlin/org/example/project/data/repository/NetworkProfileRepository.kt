package org.example.project.data.repository

import kotlinx.coroutines.flow.first
import org.example.project.domain.model.auth.User
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.repository.profile.ProfileRepository
import org.example.project.domain.repository.auth.UserSessionManager

class NetworkProfileRepository(
    private val sessionManager: UserSessionManager
) : ProfileRepository {

    override suspend fun getUserProfile(): Result<User> {
        return try {
            val isLogged = sessionManager.isUserLoggedIn.first()
            if (!isLogged) {
                return Result.failure(Exception("User is not logged in"))
            }

            val userId = sessionManager.getUserId() ?: return Result.failure(Exception("User ID not found"))
            val name = sessionManager.getUserName() ?: "Unknown User"
            val email = sessionManager.getUserEmail() ?: ""
            val role = sessionManager.getUserRole() ?: UserRole.PATIENT

            val user = User(
                id = userId,
                name = name,
                email = email,
                role = role
            )
            Result.success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            sessionManager.clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
