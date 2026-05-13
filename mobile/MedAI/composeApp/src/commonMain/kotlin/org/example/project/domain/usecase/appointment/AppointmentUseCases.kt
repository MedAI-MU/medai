package org.example.project.domain.usecase.appointment

import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.model.appointment.CancelReason
import org.example.project.domain.repository.appointment.AppointmentRepository

class GetAppointmentsUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(status: AppointmentDetailStatus): Result<List<AppointmentDetail>> =
        repository.getAppointments(status)
}

class GetAppointmentDetailsUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(): Result<List<AppointmentDetail>> =
        repository.getMyAppointments()
}

class CancelAppointmentUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(id: String): Result<Unit> =
        repository.cancelAppointment(id)
}

class SubmitReviewUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(appointmentId: String, rating: Int, review: String?): Result<Unit> =
        repository.submitReview(appointmentId, rating, review)
}

class GetCancelReasonsUseCase(private val repository: AppointmentRepository) {
    suspend operator fun invoke(): Result<List<CancelReason>> =
        repository.getCancelReasons()
}
