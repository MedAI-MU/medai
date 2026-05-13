package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.model.appointment.CancelReason
import org.example.project.domain.repository.appointment.AppointmentRepository

class MockAppointmentRepository : AppointmentRepository {

    private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    private val mockAppointments = listOf(
        AppointmentDetail(
            id = "1",
            doctorName = "Dr. Olivia Turner",
            specialty = "Dermatologist",
            doctorRating = 4.8,
            date = now, // Today
            status = AppointmentDetailStatus.UPCOMING,
            patientName = "Jane Doe",
            canRebook = false,
            canAddReview = false
        ),
        AppointmentDetail(
            id = "2",
            doctorName = "Dr. Alexander Bennett",
            specialty = "Dermatologist",
            doctorRating = 4.5,
            date = now,
            status = AppointmentDetailStatus.FINISHED,
            patientName = "Jane Doe",
            canRebook = true,
            canAddReview = true
        ),
        AppointmentDetail(
            id = "3",
            doctorName = "Dr. Michael Chang",
            specialty = "Cardiologist",
            doctorRating = 4.9,
            date = now,
            status = AppointmentDetailStatus.CANCELLED,
            patientName = "Jane Doe",
            canRebook = true,
            canAddReview = false
        )
    )

    override suspend fun getAppointments(status: AppointmentDetailStatus): Result<List<AppointmentDetail>> {
        println("MockAppointmentRepository: getAppointments called with status $status")
        delay(100)
        return Result.success(mockAppointments.filter { it.status == status })
    }

    override suspend fun getMyAppointments(): Result<List<AppointmentDetail>> {
        delay(100)
        return Result.success(mockAppointments)
    }

    override suspend fun cancelAppointment(id: String): Result<Unit> {
        delay(150)
        return Result.success(Unit)
    }

    override suspend fun submitReview(appointmentId: String, rating: Int, review: String?): Result<Unit> {
        delay(150)
        return Result.success(Unit)
    }

    override suspend fun getCancelReasons(): Result<List<CancelReason>> {
        delay(500)
        return Result.success(
            listOf(
                CancelReason("r1", "Rescheduling"),
                CancelReason("r2", "Weather Conditions"),
                CancelReason("r3", "Unexpected Work"),
                CancelReason("r4", "Others")
            )
        )
    }
}
