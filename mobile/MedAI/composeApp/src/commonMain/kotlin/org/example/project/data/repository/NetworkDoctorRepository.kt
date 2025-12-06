package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.example.project.data.remote.dto.DoctorDto
import org.example.project.domain.model.Doctor
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
}
