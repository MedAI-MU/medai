package org.example.project.domain.repository.appointment

import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.model.appointment.CancelReason

interface AppointmentRepository {
    suspend fun getAppointments(status: AppointmentDetailStatus): Result<List<AppointmentDetail>>
    suspend fun getMyAppointments(): Result<List<AppointmentDetail>>
    suspend fun cancelAppointment(id: String): Result<Unit>
    suspend fun submitReview(appointmentId: String, rating: Int, review: String?): Result<Unit>
    suspend fun getCancelReasons(): Result<List<CancelReason>>
}
