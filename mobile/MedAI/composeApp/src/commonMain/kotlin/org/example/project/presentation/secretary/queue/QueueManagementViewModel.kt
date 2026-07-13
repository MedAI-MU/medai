package org.example.project.presentation.secretary.queue

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.secretary.CheckInPatientUseCase
import org.example.project.domain.usecase.appointment.GetTodayAppointmentsUseCase
import org.example.project.domain.usecase.appointment.CancelAppointmentUseCase
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.model.secretary.QueueStatus

class QueueManagementViewModel(
    private val getTodayAppointmentsUseCase: GetTodayAppointmentsUseCase,
    private val checkInPatientUseCase: CheckInPatientUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase
) : MviScreenModel<QueueManagementState, QueueManagementEvent, QueueManagementEffect>(QueueManagementState()) {

    init {
        onEvent(QueueManagementEvent.LoadQueues)
    }

    override fun onEvent(event: QueueManagementEvent) {
        when (event) {
            is QueueManagementEvent.LoadQueues -> loadQueues()
            is QueueManagementEvent.CheckIn -> checkIn(event.appointmentId)
            is QueueManagementEvent.Cancel -> cancel(event.appointmentId)
        }
    }

    private fun loadQueues() {
        setState { copy(isLoading = true) }
        screenModelScope.launch {
            getTodayAppointmentsUseCase().fold(
                onSuccess = { appointments ->
                    val queues = appointments.map { it.toQueueEntry() }
                    setState { copy(queues = queues, isLoading = false) }
                },
                onFailure = { throwable ->
                    setState { copy(isLoading = false) }
                    sendEffect(QueueManagementEffect.ShowSnackbar(throwable.message ?: "Failed to load appointments", isError = true))
                }
            )
        }
    }

    private fun checkIn(appointmentId: String) {
        screenModelScope.launch {
            checkInPatientUseCase(appointmentId).fold(
                onSuccess = {
                    sendEffect(QueueManagementEffect.ShowSnackbar("Patient checked in successfully"))
                    loadQueues()
                },
                onFailure = { throwable ->
                    sendEffect(QueueManagementEffect.ShowSnackbar(throwable.message ?: "Failed to check in patient", isError = true))
                }
            )
        }
    }

    private fun cancel(appointmentId: String) {
        screenModelScope.launch {
            cancelAppointmentUseCase(appointmentId).fold(
                onSuccess = {
                    sendEffect(QueueManagementEffect.ShowSnackbar("Appointment rejected/cancelled successfully"))
                    loadQueues()
                },
                onFailure = { throwable ->
                    sendEffect(QueueManagementEffect.ShowSnackbar(throwable.message ?: "Failed to reject appointment", isError = true))
                }
            )
        }
    }

    private fun AppointmentDetail.toQueueEntry(): QueueEntry {
        val qStatus = when (this.status) {
            AppointmentDetailStatus.FINISHED -> QueueStatus.COMPLETED
            AppointmentDetailStatus.CANCELLED -> QueueStatus.CANCELLED
            AppointmentDetailStatus.UPCOMING -> QueueStatus.WAITING
        }
        return QueueEntry(
            id = this.id,
            patientId = this.patientId,
            patientName = this.patientName,
            doctorId = "",
            doctorName = this.doctorName,
            appointmentTime = this.date.toString(),
            status = qStatus,
            isPast = this.isPast
        )
    }
}
