package org.example.project.domain.usecase

import org.example.project.domain.model.AppointmentDetail
import org.example.project.domain.model.AppointmentDetailStatus
import org.example.project.domain.model.CancelReason
import org.example.project.domain.repository.AppointmentRepository

class GetAppointmentsUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(status: AppointmentDetailStatus): Result<List<AppointmentDetail>> =
        repository.getAppointments(status)
}

class GetAppointmentDetailsUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(id: String): Result<AppointmentDetail> =
        repository.getAppointmentDetails(id)
}

class CancelAppointmentUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(id: String, reasonId: String, otherReason: String?): Result<Unit> =
        repository.cancelAppointment(id, reasonId, otherReason)
}

class SubmitReviewUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointmentId: String, rating: Int, comment: String): Result<Unit> =
        repository.submitReview(appointmentId, rating, comment)
}

class GetCancelReasonsUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(): Result<List<CancelReason>> =
        repository.getCancelReasons()
}
