package org.example.project.data.repository

import io.ktor.client.HttpClient
import org.example.project.domain.model.AppointmentDetail
import org.example.project.domain.model.AppointmentDetailStatus
import org.example.project.domain.model.CancelReason
import org.example.project.domain.repository.AppointmentRepository

class NetworkAppointmentRepository(
    private val client: HttpClient
) : AppointmentRepository {
    // TODO: Implement actual network calls when backend is ready

    override suspend fun getAppointments(status: AppointmentDetailStatus): Result<List<AppointmentDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAppointmentDetails(id: String): Result<AppointmentDetail> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelAppointment(id: String, reasonId: String, otherReason: String?): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun submitReview(appointmentId: String, rating: Int, comment: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getCancelReasons(): Result<List<CancelReason>> {
        TODO("Not yet implemented")
    }
}
