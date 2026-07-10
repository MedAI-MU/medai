package org.example.project.presentation.secretary.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.model.patient.Patient
import org.example.project.domain.model.secretary.QueueStatus
import org.example.project.domain.usecase.secretary.CheckInPatientUseCase
import org.example.project.domain.usecase.secretary.CreatePatientUseCase
import org.example.project.domain.usecase.appointment.GetTodayAppointmentsUseCase
import org.example.project.domain.usecase.profile.GetProfileUseCase
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.model.secretary.ClinicStats
import org.example.project.domain.model.secretary.QueueEntry

class SecretaryDashboardViewModel(
    private val getTodayAppointmentsUseCase: GetTodayAppointmentsUseCase,
    private val checkInPatientUseCase: CheckInPatientUseCase,
    private val createPatientUseCase: CreatePatientUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : MviScreenModel<SecretaryDashboardState, SecretaryDashboardEvent, SecretaryDashboardEffect>(SecretaryDashboardState()) {

    init {
        onEvent(SecretaryDashboardEvent.LoadDashboard)
        loadSecretaryProfile()
    }

    private fun loadSecretaryProfile() {
        screenModelScope.launch {
            getProfileUseCase().fold(
                onSuccess = { user ->
                    setState { copy(secretaryName = user.name, avatarUrl = user.avatarUrl) }
                },
                onFailure = {}
            )
        }
    }

    override fun onEvent(event: SecretaryDashboardEvent) {
        when (event) {
            is SecretaryDashboardEvent.LoadDashboard -> {
                loadDashboardData()
            }
            is SecretaryDashboardEvent.UpdateQueueStatus -> {
                updateQueueStatus(event.entryId, event.status)
            }
            is SecretaryDashboardEvent.CreatePatient -> {
                createPatient(event.patient)
            }
        }
    }

    private fun loadDashboardData() {
        setState { copy(isLoading = true) }
        screenModelScope.launch {
            getTodayAppointmentsUseCase().fold(
                onSuccess = { appointments ->
                    val queueEntries = appointments.map { it.toQueueEntry() }
                    val activeDocCount = appointments.map { it.doctorName }.distinct().size
                    val pendingCount = appointments.count { it.status == AppointmentDetailStatus.UPCOMING }

                    setState {
                        copy(
                            queues = queueEntries,
                            clinicStats = ClinicStats(
                                totalPatientsToday = queueEntries.size,
                                activeDoctors = activeDocCount,
                                totalRevenueToday = 150.0, // Mocked revenue today
                                pendingAppointments = pendingCount
                            ),
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { throwable ->
                    setState { copy(isLoading = false, error = throwable.message) }
                }
            )
        }
    }

    private fun updateQueueStatus(entryId: String, status: QueueStatus) {
        screenModelScope.launch {
            if (status == QueueStatus.WAITING) {
                checkInPatientUseCase(entryId).fold(
                    onSuccess = {
                        sendEffect(SecretaryDashboardEffect.ShowSnackbar("Patient checked in successfully"))
                        loadDashboardData()
                    },
                    onFailure = { throwable ->
                        sendEffect(SecretaryDashboardEffect.ShowSnackbar(throwable.message ?: "Failed to check in patient", isError = true))
                    }
                )
            }
        }
    }

    private fun createPatient(patient: Patient) {
        screenModelScope.launch {
            val result = createPatientUseCase(patient)
            if (result.isSuccess) {
                sendEffect(SecretaryDashboardEffect.ShowSnackbar("Patient created successfully"))
            } else {
                setState { copy(error = result.exceptionOrNull()?.message) }
                sendEffect(SecretaryDashboardEffect.ShowSnackbar(result.exceptionOrNull()?.message ?: "Failed to create patient", isError = true))
            }
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
            status = qStatus
        )
    }
}
