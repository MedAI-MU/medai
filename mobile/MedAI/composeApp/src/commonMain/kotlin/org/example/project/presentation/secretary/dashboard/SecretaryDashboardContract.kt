package org.example.project.presentation.secretary.dashboard

import org.example.project.domain.model.patient.Patient
import org.example.project.domain.model.secretary.ClinicStats
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.model.secretary.QueueStatus

data class SecretaryDashboardState(
    val secretaryName: String = "",
    val avatarUrl: String? = null,
    val clinicStats: ClinicStats = ClinicStats(0, 0, 0.0, 0),
    val queues: List<QueueEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SecretaryDashboardEvent {
    object LoadDashboard : SecretaryDashboardEvent()
    data class UpdateQueueStatus(val entryId: String, val status: QueueStatus) : SecretaryDashboardEvent()
    data class CreatePatient(val patient: Patient) : SecretaryDashboardEvent()
}

sealed class SecretaryDashboardEffect {
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : SecretaryDashboardEffect()
}
