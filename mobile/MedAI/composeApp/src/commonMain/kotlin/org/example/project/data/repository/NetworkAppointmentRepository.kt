package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import io.ktor.client.request.post
import org.example.project.data.remote.dto.appointments.AppointmentDto
import org.example.project.data.remote.dto.appointments.CancelAppointmentRequest
import org.example.project.data.remote.dto.appointments.CreateAppointmentRequest
import org.example.project.domain.model.AppointmentDetail
import org.example.project.domain.model.AppointmentDetailStatus
import org.example.project.domain.model.CancelReason
import org.example.project.domain.repository.AppointmentRepository

class NetworkAppointmentRepository(
    private val client: HttpClient
) : AppointmentRepository {

    override suspend fun getAppointments(status: AppointmentDetailStatus): Result<List<AppointmentDetail>> {
        return try {
            val response = client.get("/appointments")
            if (response.status.isSuccess()) {
                val dtos: List<AppointmentDto> = response.body()
                val mapped = dtos.map { it.toDomain() }.filter {
                    when (status) {
                        AppointmentDetailStatus.UPCOMING -> it.status == AppointmentDetailStatus.UPCOMING
                        AppointmentDetailStatus.COMPLETED -> it.status == AppointmentDetailStatus.COMPLETED
                        AppointmentDetailStatus.CANCELLED -> it.status == AppointmentDetailStatus.CANCELLED
                    }
                }
                Result.success(mapped)
            } else {
                Result.failure(Exception("Failed to fetch appointments: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDoctorAppointments(date: Long): Result<List<AppointmentDetail>> {
        return try {
            val response = client.get("/appointments")
            if (response.status.isSuccess()) {
                val dtos: List<AppointmentDto> = response.body()
                val targetDate = kotlinx.datetime.Instant.fromEpochMilliseconds(date)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date

                val mapped = dtos.map { it.toDomain() }.filter {
                    it.date.date == targetDate
                }
                Result.success(mapped)
            } else {
                Result.failure(Exception("Failed to fetch doctor appointments: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentDetails(id: String): Result<AppointmentDetail> {
        return try {
            val response = client.get("/appointments/$id")
            if (response.status.isSuccess()) {
                val dto: AppointmentDto = response.body()
                Result.success(dto.toDomain())
            } else {
                Result.failure(Exception("Failed to fetch appointment details: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAppointment(id: String, reasonId: String, otherReason: String?): Result<Unit> {
        return try {
            val response = client.patch("/appointments/$id/cancel") {
                contentType(ContentType.Application.Json)
                setBody(CancelAppointmentRequest(reasonId = reasonId, otherReason = otherReason))
            }
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to cancel appointment: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitReview(appointmentId: String, rating: Int, comment: String): Result<Unit> {
        // Implement when review endpoint exists
        return Result.success(Unit)
    }

    override suspend fun getCancelReasons(): Result<List<CancelReason>> {
        return Result.success(
            listOf(
                CancelReason("1", "I have a scheduling conflict"),
                CancelReason("2", "I am feeling better"),
                CancelReason("3", "Emergency situation"),
                CancelReason("4", "Other")
            )
        )
    }

    override suspend fun createAppointment(doctorId: Int, slotId: Int, problemDescription: String?): Result<Unit> {
        return try {
            val response = client.post("/appointments") {
                contentType(ContentType.Application.Json)
                setBody(CreateAppointmentRequest(doctorId = doctorId, slotId = slotId, problemDescription = problemDescription))
            }
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create appointment: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun AppointmentDto.toDomain(): AppointmentDetail {
        val doctorUser = doctor?.user
        val patientUser = patient?.user
        val doctorNameStr = doctorUser?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Doctor"
        val patientNameStr = patientUser?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Patient"

        val dateObj = try {
            val dayDate = slot?.schedule?.dayDate ?: "2024-01-01"
            val startTime = slot?.startTime ?: "00:00:00"
            // startTime might be "HH:mm" or "HH:mm:ss"
            val parsedTime = if (startTime.count { it == ':' } == 1) "$startTime:00" else startTime
            LocalDateTime.parse("${dayDate}T${parsedTime}")
        } catch (e: Exception) {
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }

        val domainStatus = when (status) {
            "upcoming" -> AppointmentDetailStatus.UPCOMING
            "completed" -> AppointmentDetailStatus.COMPLETED
            "cancelled" -> AppointmentDetailStatus.CANCELLED
            else -> AppointmentDetailStatus.UPCOMING
        }

        return AppointmentDetail(
            id = id.toString(),
            doctorName = doctorNameStr,
            specialty = "General", // Placeholder, since it's not in the DTO
            doctorRating = 5.0, // Placeholder
            date = dateObj,
            status = domainStatus,
            patientName = patientNameStr,
            patientAge = patientUser?.dateOfBirth?.let { "..." } ?: "N/A", // We can calculate age if we want
            patientGender = patientUser?.gender ?: "Unknown",
            problemDescription = problemDescription ?: "",
            canRebook = domainStatus == AppointmentDetailStatus.CANCELLED,
            canAddReview = domainStatus == AppointmentDetailStatus.COMPLETED && rating == null
        )
    }
}
