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
import org.example.project.data.remote.dto.appointment.AppointmentResponseDto
import org.example.project.data.remote.dto.appointment.ReviewAppointmentRequestDto
import org.example.project.data.remote.dto.appointment.UpdateAppointmentStatusRequestDto
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.model.appointment.CancelReason
import org.example.project.domain.repository.appointment.AppointmentRepository

class NetworkAppointmentRepository(
    private val client: HttpClient
) : AppointmentRepository {

    override suspend fun getAppointments(status: AppointmentDetailStatus): Result<List<AppointmentDetail>> {
        return try {
            val response: List<AppointmentResponseDto> = client.get("appointments/me").body()

            val filtered = response.filter { dto ->
                when (status) {
                    AppointmentDetailStatus.UPCOMING -> dto.status == "pending" || dto.status == "confirmed"
                    AppointmentDetailStatus.FINISHED -> dto.status == "finished"
                    AppointmentDetailStatus.CANCELLED -> dto.status == "cancelled"
                }
            }.map { it.toDomain() }

            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyAppointments(): Result<List<AppointmentDetail>> {
        return try {
            val response: List<AppointmentResponseDto> = client.get("appointments/me").body()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAppointment(id: String): Result<Unit> {
        return try {
            client.patch("appointments/$id/status") {
                contentType(ContentType.Application.Json)
                setBody(UpdateAppointmentStatusRequestDto(status = "cancelled"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitReview(appointmentId: String, rating: Int, review: String?): Result<Unit> {
        return try {
            client.patch("appointments/$appointmentId/review") {
                contentType(ContentType.Application.Json)
                setBody(ReviewAppointmentRequestDto(rating = rating, review = review))
            }
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

    private fun AppointmentResponseDto.toDomain(): AppointmentDetail {
        val apptStatus = when (this.status) {
            "finished" -> AppointmentDetailStatus.FINISHED
            "cancelled" -> AppointmentDetailStatus.CANCELLED
            else -> AppointmentDetailStatus.UPCOMING
        }

        var dateValue = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        try {
            val dateStr = this.createdAt.take(10)
            dateValue = LocalDateTime.parse("${dateStr}T00:00:00")
        } catch (e: Exception) {
            // fallback to current time
        }

        return AppointmentDetail(
            id = this.id.toString(),
            doctorName = "Doctor #${this.doctorUserId}",
            specialty = "General",
            doctorRating = 0.0,
            date = dateValue,
            status = apptStatus,
            patientName = "Patient #${this.patientUserId}",
            canRebook = apptStatus == AppointmentDetailStatus.CANCELLED || apptStatus == AppointmentDetailStatus.FINISHED,
            canAddReview = apptStatus == AppointmentDetailStatus.FINISHED && this.rating == null,
            rating = this.rating,
            review = this.review
        )
    }
}
