package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.flow.first
import org.example.project.data.remote.dto.doctor.DoctorResponseDto
import org.example.project.data.remote.dto.user.AvatarUploadResponseDto
import org.example.project.data.remote.dto.user.UpdateUserRequestDto
import org.example.project.data.remote.dto.user.UserProfileDto
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.auth.AccountStatus
import org.example.project.domain.model.auth.User
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.repository.profile.ProfileRepository

class NetworkProfileRepository(
    private val client: HttpClient,
    private val sessionManager: UserSessionManager
) : ProfileRepository {

    override suspend fun getUserProfile(): Result<User> {
        return try {
            val isLogged = sessionManager.isUserLoggedIn.first()
            if (!isLogged) {
                return Result.failure(Exception("User is not logged in"))
            }

            // Fetch general profile from GET /users/me
            val userDto: UserProfileDto = client.get("users/me").body()

            // Fetch about details specifically from GET /doctors/{id} if user is a doctor
            val about = if (userDto.role.lowercase() == "doctor") {
                try {
                    val doctorDto: DoctorResponseDto = client.get("doctors/${userDto.id}").body()
                    doctorDto.about
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            } else {
                null
            }

            val user = userDto.toDomain(about)

            // Cache details in sessionManager
            val currentToken = sessionManager.getUserToken() ?: ""
            sessionManager.saveSession(
                userId = user.id,
                token = currentToken,
                name = user.name,
                email = user.email,
                role = user.role,
                accountStatus = when (userDto.status.lowercase()) {
                    "pending" -> AccountStatus.PENDING
                    "approved" -> AccountStatus.APPROVED
                    else -> AccountStatus.APPROVED
                }
            )

            Result.success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(
        userId: String,
        name: String?,
        phone: String?,
        birthDate: String?,
        gender: String?,
        bio: String?
    ): Result<User> {
        return try {
            val requestDto = UpdateUserRequestDto(
                name = name,
                phone = phone,
                birthDate = birthDate,
                gender = gender,
                bio = bio
            )

            // PATCH /users/{userId}
            val responseDto: UserProfileDto = client.patch("users/$userId") {
                contentType(ContentType.Application.Json)
                setBody(requestDto)
            }.body()

            // Fetch updated about details if doctor
            val about = if (responseDto.role.lowercase() == "doctor") {
                try {
                    val doctorDto: DoctorResponseDto = client.get("doctors/${responseDto.id}").body()
                    doctorDto.about
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            } else {
                null
            }

            val user = responseDto.toDomain(about)

            // Sync updated details with sessionManager
            val currentToken = sessionManager.getUserToken() ?: ""
            sessionManager.saveSession(
                userId = user.id,
                token = currentToken,
                name = user.name,
                email = user.email,
                role = user.role,
                accountStatus = when (responseDto.status.lowercase()) {
                    "pending" -> AccountStatus.PENDING
                    "approved" -> AccountStatus.APPROVED
                    else -> AccountStatus.APPROVED
                }
            )

            Result.success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(
        userId: String,
        imageBytes: ByteArray,
        fileName: String
    ): Result<String> {
        return try {
            // POST /users/{id}/avatar with multipart form data
            val response: AvatarUploadResponseDto = client.post("users/$userId/avatar") {
                headers.remove(HttpHeaders.ContentType)
                setBody(MultiPartFormDataContent(
                    formData {
                        append("file", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        })
                    }
                ))
            }.body()

            Result.success(response.avatar)
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
