package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDate
import org.example.project.data.remote.dto.DoctorDto
import org.example.project.data.remote.dto.TimeSlotDto
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.Doctor
import org.example.project.domain.model.TimeSlot
import org.example.project.domain.repository.DoctorRepository

class NetworkDoctorRepository(
    private val client: HttpClient
) : DoctorRepository {

    override suspend fun getDoctors(specialtyId: String?): Result<List<Doctor>> {
        return try {
            // GET /doctors (optionally filtered by specialty_id query param)
            val response: List<DoctorDto> = client.get("/doctors") {
                if (specialtyId != null) {
                    parameter("specialty_id", specialtyId)
                }
            }.body()

            val domainList = response.map { dto ->
                Doctor(
                    id = dto.id,
                    name = dto.name,
                    specialty = dto.specialty,
                    rating = dto.rating,
                    imageUrl = dto.imageUrl
                )
            }
            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDoctorById(doctorId: String): Result<Doctor> {
        return try {
            // GET /doctors/{id}
            val dto: DoctorDto = client.get("/doctors/$doctorId").body()
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableSlots(doctorId: String, date: LocalDate): Result<List<TimeSlot>> {
        return try {
            val response: List<TimeSlotDto> = client.get("/doctors/$doctorId/slots") {
                parameter("date", date.toString())
            }.body()

            Result.success(response.map { dto ->
                TimeSlot(dto.id, dto.time, dto.isAvailable)
            })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
