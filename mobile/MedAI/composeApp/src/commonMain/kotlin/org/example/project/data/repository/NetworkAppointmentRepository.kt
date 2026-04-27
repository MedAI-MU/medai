package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.remote.dto.AppointmentDto
import org.example.project.data.remote.dto.UpdateAppointmentStatusRequestDto
import org.example.project.domain.model.AppointmentDetail
import org.example.project.domain.model.AppointmentDetailStatus
import org.example.project.domain.model.CancelReason
import org.example.project.domain.repository.AppointmentRepository

class NetworkAppointmentRepository(
    private val client: HttpClient
) : AppointmentRepository {

    override suspend fun getAppointments(status: AppointmentDetailStatus): Result<List<AppointmentDetail>> {
        return try {
            val response: List<AppointmentDto> = client.get("appointments/my-appointments").body()

            // Filter by requested status
            val filtered = response.filter { dto ->
                when (status) {
                    AppointmentDetailStatus.UPCOMING -> dto.status == "pending" || dto.status == "confirmed"
                    AppointmentDetailStatus.COMPLETED -> dto.status == "completed"
                    AppointmentDetailStatus.CANCELLED -> dto.status == "cancelled"
                }
            }.map { it.toDomain() }

            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDoctorAppointments(date: Long): Result<List<AppointmentDetail>> {
        return try {
            val response: List<AppointmentDto> = client.get("appointments/doctor-appointments").body()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppointmentDetails(id: String): Result<AppointmentDetail> {
        return try {
            val response: AppointmentDto = client.get("appointments/$id").body()
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAppointment(id: String, reasonId: String, otherReason: String?): Result<Unit> {
        return try {
            client.patch("appointments/$id/status") {
                contentType(ContentType.Application.Json)
                setBody(UpdateAppointmentStatusRequestDto(
                    status = "cancelled",
                    cancellationReason = otherReason ?: reasonId
                ))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitReview(appointmentId: String, rating: Int, comment: String): Result<Unit> {
        return try {
            // Note: If you implement a separate review endpoint, use it here.
            // For now, this is a placeholder mimicking success.
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCancelReasons(): Result<List<CancelReason>> {
        return Result.success(listOf(
            CancelReason("1", "I have a scheduling conflict"),
            CancelReason("2", "I feel better now"),
            CancelReason("3", "Found another doctor"),
            CancelReason("4", "Other")
        ))
    }

    private fun AppointmentDto.toDomain(): AppointmentDetail {
        val apptStatus = when (this.status) {
            "completed" -> AppointmentDetailStatus.COMPLETED
            "cancelled" -> AppointmentDetailStatus.CANCELLED
            else -> AppointmentDetailStatus.UPCOMING
        }

        // Parse date and time if available
        var dateValue = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        try {
            // First attempt to grab date from schedule day if your API returns it there,
            // else use createdAt for testing or current time.
            val dateStr = this.createdAt.take(10)
            val timeStr = this.slot?.startTime ?: "00:00:00"
            dateValue = LocalDateTime.parse("${dateStr}T${timeStr}")
        } catch (e: Exception) {
            // fallback to current time
        }

        return AppointmentDetail(
            id = this.id.toString(),
            doctorName = this.doctor?.name ?: "Unknown Doctor",
            specialty = this.doctor?.specialty ?: "General",
            doctorRating = this.doctor?.rating ?: 0.0,
            date = dateValue,
            status = apptStatus,
            patientName = this.bookedForName ?: this.patient?.user?.name ?: "Patient",
            patientAge = this.bookedForAge ?: "N/A",
            patientGender = this.bookedForGender ?: this.patient?.user?.gender?.name ?: "N/A",
            problemDescription = this.problemDescription ?: "No description provided",
            canRebook = apptStatus == AppointmentDetailStatus.CANCELLED || apptStatus == AppointmentDetailStatus.COMPLETED,
            canAddReview = apptStatus == AppointmentDetailStatus.COMPLETED && this.rating == null
        )
    }
}
