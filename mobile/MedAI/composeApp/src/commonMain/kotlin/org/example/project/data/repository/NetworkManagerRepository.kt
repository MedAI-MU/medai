package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.data.remote.dto.doctor.DoctorResponseDto
import org.example.project.data.remote.dto.manager.ApproveUserRequestDto
import org.example.project.data.remote.dto.manager.ManagedUserDto
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.manager.ManagedUser
import org.example.project.domain.repository.manager.ManagerRepository

class NetworkManagerRepository(
    private val client: HttpClient
) : ManagerRepository {

    override suspend fun getPendingDoctors(): Result<List<ManagedUser>> {
        return try {
            val response = client.get("users/pending/doctors").body<List<ManagedUserDto>>()
            val domainList = response.map { it.toDomain() }
            Result.success(domainList)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getPendingSecretaries(): Result<List<ManagedUser>> {
        return try {
            val response = client.get("users/pending/secretaries").body<List<ManagedUserDto>>()
            val domainList = response.map { it.toDomain() }
            Result.success(domainList)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getApprovedDoctors(): Result<List<ManagedUser>> {
        return try {
            val response = client.get("doctors").body<List<DoctorResponseDto>>()
            val domainList = response.map { dto ->
                ManagedUser(
                    id = dto.userId,
                    name = dto.name ?: "Unknown",
                    role = "doctor",
                    status = "approved"
                )
            }
            Result.success(domainList)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getApprovedSecretaries(): Result<List<ManagedUser>> {
        return try {
            val response = client.get("users/secretaries").body<List<ManagedUserDto>>()
            val domainList = response.map { dto ->
                ManagedUser(
                    id = dto.id,
                    name = dto.name,
                    role = "secretary",
                    status = "approved"
                )
            }
            Result.success(domainList)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun approveDoctor(userId: Int): Result<Unit> {
        return try {
            client.post("users/add/doctor") {
                contentType(ContentType.Application.Json)
                setBody(ApproveUserRequestDto(userId))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun approveSecretary(userId: Int): Result<Unit> {
        return try {
            client.post("users/add/secretary") {
                contentType(ContentType.Application.Json)
                setBody(ApproveUserRequestDto(userId))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun removeUser(userId: Int): Result<Unit> {
        return try {
            client.delete("users/$userId")
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
