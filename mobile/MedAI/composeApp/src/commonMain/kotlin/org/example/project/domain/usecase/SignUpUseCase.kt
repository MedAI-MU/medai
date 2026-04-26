package org.example.project.domain.usecase

import org.example.project.data.remote.dto.SignUpRequest
import org.example.project.data.remote.dto.SignUpResponse
import org.example.project.domain.repository.SignUpRepository
import org.example.project.domain.model.UserRole
import org.example.project.domain.repository.UserSessionManager

class SignUpUseCase(
    private val repository: SignUpRepository,
    private val sessionManager: UserSessionManager
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        pass: String,
        mobile: String,
        dob: String,
        role: UserRole
    ): Result<SignUpResponse> {
        // 1. Business Logic Validation
        if (fullName.isBlank()) return Result.failure(Exception("Name is required"))
        if (email.isBlank() || !email.contains("@")) return Result.failure(Exception("Invalid Email"))
        if (pass.length < 6) return Result.failure(Exception("Password must be at least 6 chars"))

        // 2. Map to DTO
        val request = SignUpRequest(
            name = fullName,
            email = email,
            password = pass,
            phone = mobile,
            //dob = dob,
            role = role.name.lowercase() // Convert Enum to String
        )

        // 3. Call Repository
        val result = repository.register(request)

        // 4. If Success -> Save to Session Manager
        return result.map { signUpData ->
            // Try to parse role safely
            val userRole = try {
                UserRole.valueOf(signUpData.role.lowercase())
            } catch (e: Exception) {
                UserRole.PATIENT // Fallback
            }

            sessionManager.saveSession(
                userId = signUpData.userId,
                token = signUpData.token,
                name = fullName, // We can use the requested name as fallback or from response if available
                role = userRole
            )
            signUpData
        }
    }
}
