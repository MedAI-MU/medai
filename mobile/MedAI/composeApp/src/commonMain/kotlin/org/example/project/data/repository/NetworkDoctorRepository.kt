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
            val fromDate = date.toString()
            val toDate = date.toString()

            val dtoResult: org.example.project.data.remote.dto.schedule.DocScheduleResponseDto = client.get("/doctors/$doctorId/schedule-slots") {
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
        slotId: String,
        date: LocalDate,
        patientName: String,
        patientAge: String,
        patientGender: String,
        problemDescription: String
    ): Result<String> {
        return try {
            val request = BookingRequestDto(
                doctorId = doctorId.toIntOrNull() ?: 0,
                slotId = slotId.toIntOrNull() ?: 0,
                bookedForName = patientName.ifBlank { null },
                bookedForAge = patientAge.ifBlank { null },
                bookedForGender = patientGender.ifBlank { null },
                problemDescription = problemDescription.ifBlank { null }
            )

            // POST /appointments
            val response: BookingResponseDto = client.post("/appointments") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            Result.success(response.id.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
