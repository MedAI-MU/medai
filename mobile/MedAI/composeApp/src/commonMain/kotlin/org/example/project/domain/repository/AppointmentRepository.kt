package org.example.project.domain.repository

import org.example.project.domain.model.AppointmentDetail
import org.example.project.domain.model.AppointmentDetailStatus
import org.example.project.domain.model.CancelReason

interface AppointmentRepository {
    suspend fun getAppointments(status: AppointmentDetailStatus): Result<List<AppointmentDetail>>
    suspend fun getAppointmentDetails(id: String): Result<AppointmentDetail>
    suspend fun cancelAppointment(id: String, reasonId: String, otherReason: String?): Result<Unit>
    suspend fun submitReview(appointmentId: String, rating: Int, comment: String): Result<Unit>
    suspend fun getCancelReasons(): Result<List<CancelReason>>
}
