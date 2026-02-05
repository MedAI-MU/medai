package org.example.project.domain.usecase

import org.example.project.data.remote.dto.SignUpRequest
import org.example.project.data.remote.dto.SignUpResponse
import org.example.project.domain.repository.SignUpRepository
import org.example.project.domain.model.UserRole

class SignUpUseCase(
    private val repository: SignUpRepository
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
            fullName = fullName,
            email = email,
            password = pass,
            mobile = mobile,
            dob = dob,
            role = role.name // Convert Enum to String
        )

        // 3. Call Repository
        return repository.register(request)
    }
}
