package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.LocalDate
import org.example.project.data.remote.dto.appointment.AppointmentResponseDto
import org.example.project.data.remote.dto.appointment.BookingRequestDto
import org.example.project.data.remote.dto.doctor.AssignSpecialityRequestDto
import org.example.project.data.remote.dto.doctor.DoctorResponseDto
import org.example.project.data.remote.dto.doctor.SearchSpecialityRequestDto
import org.example.project.data.remote.dto.doctor.SpecialityItemResponseDto
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.doctor.Doctor
import org.example.project.domain.model.doctor.Speciality
import org.example.project.domain.model.appointment.TimeSlot
import org.example.project.domain.repository.doctor.DoctorRepository

class NetworkDoctorRepository(
    private val client: HttpClient
) : DoctorRepository {

    override suspend fun getDoctors(specialtyId: String?): Result<List<Doctor>> {
        return try {
            val response: List<DoctorResponseDto> = if (specialtyId != null) {
                // Fetch by specialty name using POST
                client.post("doctors/search/speciality") {
                    contentType(ContentType.Application.Json)
                    setBody(SearchSpecialityRequestDto(name = specialtyId))
                }.body()
            } else {
                // Fetch all doctors
                client.get("doctors").body()
            }

            val domainList = response.map { dto ->
                dto.toDomain()
            }
            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDoctorById(doctorId: String): Result<Doctor> {
        return try {
            // GET doctors/{id}
            val dto: DoctorResponseDto = client.get("doctors/$doctorId").body()
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
            val response: AppointmentResponseDto = client.post("appointments") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            Result.success(response.id.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllSpecialities(): Result<List<Speciality>> {
        return try {
            val response: List<SpecialityItemResponseDto> = client.get("doctors/specialities").body()
            val specialities = response.map { Speciality(id = it.id, name = it.name) }
            Result.success(specialities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createSpeciality(name: String): Result<Speciality> {
        return try {
            val response: SpecialityItemResponseDto = client.post("doctors/specialities") {
                contentType(ContentType.Application.Json)
                setBody(SearchSpecialityRequestDto(name = name))
            }.body()
            Result.success(Speciality(id = response.id, name = response.name))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun assignSpeciality(
        doctorId: String,
        specialityId: Int,
        isPrimary: Boolean,
        yearsOfExperience: Int
    ): Result<Unit> {
        return try {
            client.post("doctors/$doctorId/specialities") {
                contentType(ContentType.Application.Json)
                setBody(AssignSpecialityRequestDto(
                    specialityId = specialityId,
                    isPrimary = isPrimary,
                    yearsOfExperience = yearsOfExperience
                ))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeSpeciality(
        doctorId: String,
        doctorSpecialityId: Int
    ): Result<Unit> {
        return try {
            client.delete("doctors/$doctorId/specialities/$doctorSpecialityId")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
