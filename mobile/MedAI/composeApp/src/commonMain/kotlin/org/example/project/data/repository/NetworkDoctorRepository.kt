package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.LocalDate
import org.example.project.data.remote.dto.BookingRequestDto
import org.example.project.data.remote.dto.BookingResponseDto
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
            val response: List<DoctorDto> = if (specialtyId != null) {
                // Fetch by specialty name using POST
                client.post("doctors/search/speciality") {
                    contentType(ContentType.Application.Json)
                    setBody(org.example.project.data.remote.dto.SearchSpecialtyRequestDto(name = specialtyId))
                }.body()
            } else {
                // Fetch all doctors
                client.get("doctors").body()
            }

            val domainList = response.map { dto ->
                val primarySpec = dto.specialities.find { it.isPrimary }?.speciality?.name
                    ?: dto.specialities.firstOrNull()?.speciality?.name
                    ?: dto.specialty
                    ?: "General"

                Doctor(
                    id = dto.id,
                    name = dto.user?.name ?: dto.name ?: "Unknown",
                    specialty = primarySpec,
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
            // GET doctors/{id}
            val dto: DoctorDto = client.get("doctors/$doctorId").body()
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableSlots(doctorId: String, date: LocalDate): Result<List<TimeSlot>> {
        return try {
            val fromDate = date.toString()
            val toDate = date.toString()

            val dtoResult: org.example.project.data.remote.dto.schedule.DocScheduleResponseDto = client.get("doctors/$doctorId/schedule-slots") {
                parameter("fromDate", fromDate)
                parameter("toDate", toDate)
            }.body()

            val timeSlots = mutableListOf<TimeSlot>()
            dtoResult.days.data.firstOrNull()?.slots?.forEach { slot ->
                timeSlots.add(TimeSlot(slot.id.toString(), slot.startTime, slot.status == "available"))
            }

            Result.success(timeSlots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun bookAppointment(
        doctorId: String,
        slotId: String
    ): Result<String> {
        return try {
            val request = BookingRequestDto(
                doctorId = doctorId.toIntOrNull() ?: 0,
                slotId = slotId.toIntOrNull() ?: 0
            )

            // POST appointments
            val response: BookingResponseDto = client.post("appointments") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            Result.success(response.id.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
