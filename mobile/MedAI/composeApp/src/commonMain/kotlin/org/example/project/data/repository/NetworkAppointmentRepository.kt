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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.example.project.data.remote.dto.appointment.AppointmentResponseDto
import org.example.project.data.remote.dto.appointment.ReviewAppointmentRequestDto
import org.example.project.data.remote.dto.appointment.UpdateAppointmentStatusRequestDto
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.model.appointment.CancelReason
import org.example.project.domain.repository.appointment.AppointmentRepository
import org.example.project.domain.repository.patient.PatientRepository
import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.model.auth.UserRole
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class NetworkAppointmentRepository(
    private val client: HttpClient,
    private val patientRepository: PatientRepository,
    private val sessionManager: UserSessionManager
) : AppointmentRepository {

    private val _refreshSignals = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val appointmentsRefreshSignals: Flow<Unit> = _refreshSignals.asSharedFlow()

    override fun triggerAppointmentsRefresh() {
        _refreshSignals.tryEmit(Unit)
    }

    private suspend fun resolvePatientNames(appointments: List<AppointmentResponseDto>): Map<String, String> {
        val uniquePatientIds = appointments.map { it.patientUserId.toString() }.distinct()
        if (uniquePatientIds.isEmpty()) return emptyMap()

        val role = sessionManager.getUserRole()
        return try {
            when (role) {
                UserRole.SECRETARY, UserRole.MANAGER -> {
                    val patientsResult = patientRepository.getPatients()
                    patientsResult.getOrNull()?.associate { it.id to it.fullName } ?: emptyMap()
                }
                UserRole.DOCTOR -> {
                    coroutineScope {
                        uniquePatientIds.map { id ->
                            async {
                                val patientName = patientRepository.getPatientById(id)
                                    .getOrNull()?.fullName ?: "Patient #$id"
                                id to patientName
                            }
                        }.awaitAll().toMap()
                    }
                }
                UserRole.PATIENT -> {
                    val currentName = sessionManager.getUserName() ?: "Patient #${uniquePatientIds.first()}"
                    uniquePatientIds.associateWith { currentName }
                }
                else -> emptyMap()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    override suspend fun getAppointments(status: AppointmentDetailStatus): Result<List<AppointmentDetail>> {
        return try {
            val response: List<AppointmentResponseDto> = client.get("appointments/me").body()
            val patientNames = resolvePatientNames(response)
            val mapped = response.map { it.toDomain(patientNames) }

            val filtered = mapped.filter { appt ->
                when (status) {
                    AppointmentDetailStatus.UPCOMING -> {
                        appt.status == AppointmentDetailStatus.UPCOMING && !appt.isPast
                    }
                    AppointmentDetailStatus.FINISHED -> {
                        appt.status == AppointmentDetailStatus.FINISHED ||
                        (appt.status == AppointmentDetailStatus.UPCOMING && appt.isPast && appt.originalStatus == "confirmed")
                    }
                    AppointmentDetailStatus.CANCELLED -> {
                        appt.status == AppointmentDetailStatus.CANCELLED ||
                        (appt.status == AppointmentDetailStatus.UPCOMING && appt.isPast && appt.originalStatus == "pending")
                    }
                }
            }

            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyAppointments(): Result<List<AppointmentDetail>> {
        return try {
            val response: List<AppointmentResponseDto> = client.get("appointments/me").body()
            val patientNames = resolvePatientNames(response)
            Result.success(response.map { it.toDomain(patientNames) })
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
            triggerAppointmentsRefresh()
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

    override suspend fun getAllAppointments(): Result<List<AppointmentDetail>> {
        return try {
            val response: List<AppointmentResponseDto> = client.get("appointments").body()
            val patientNames = resolvePatientNames(response)
            Result.success(response.map { it.toDomain(patientNames) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAppointmentStatus(id: String, status: String): Result<Unit> {
        return try {
            client.patch("appointments/$id/status") {
                contentType(ContentType.Application.Json)
                setBody(UpdateAppointmentStatusRequestDto(status = status))
            }
            triggerAppointmentsRefresh()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun AppointmentResponseDto.toDomain(patientNames: Map<String, String>): AppointmentDetail {
        val apptStatus = when (this.status) {
            "finished" -> AppointmentDetailStatus.FINISHED
            "cancelled" -> AppointmentDetailStatus.CANCELLED
            else -> AppointmentDetailStatus.UPCOMING
        }

        val dateStr = this.scheduleSlot?.schedule?.dayDate ?: this.createdAt.take(10)

        val timeStr = this.scheduleSlot?.startTime ?: "00:00:00"

        val dateValue = try {
            LocalDateTime.parse("${dateStr}T${timeStr}")
        } catch (e: Exception) {
            LocalDateTime(2000, 1, 1, 0, 0)
        }

        val primarySpec = this.doctor?.specialities?.find { it.isPrimary }?.speciality?.name
            ?: this.doctor?.specialities?.firstOrNull()?.speciality?.name
            ?: "General"

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val isPastVal = dateValue < now

        val resolvedPatientName = patientNames[this.patientUserId.toString()]
            ?: this.patient?.name
            ?: "Patient #${this.patientUserId}"

        val canAddReviewVal = (apptStatus == AppointmentDetailStatus.FINISHED || (apptStatus == AppointmentDetailStatus.UPCOMING && isPastVal && this.status == "confirmed")) && this.rating == null
        val canRebookVal = apptStatus == AppointmentDetailStatus.CANCELLED || apptStatus == AppointmentDetailStatus.FINISHED || (apptStatus == AppointmentDetailStatus.UPCOMING && isPastVal)

        return AppointmentDetail(
            id = this.id.toString(),
            patientId = this.patientUserId.toString(),
            doctorName = this.doctor?.name ?: "Doctor #${this.doctorUserId}",
            specialty = primarySpec,
            doctorRating = 0.0,
            date = dateValue,
            status = apptStatus,
            patientName = resolvedPatientName,
            canRebook = canRebookVal,
            canAddReview = canAddReviewVal,
            rating = this.rating,
            review = this.review,
            isPast = isPastVal,
            originalStatus = this.status
        )
    }
}
