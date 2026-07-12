package org.example.project.presentation.reportAnalysis

import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.report_analysis.ReportAnalysis

data class ReportAnalysisState(
    val isLoading: Boolean = false,
    val reports: List<ReportAnalysis> = emptyList(),
    val appointments: List<AppointmentDetail> = emptyList(),
    val currentUserId: String? = null,
    val currentUserRole: UserRole? = null,
    val error: String? = null,

    // Upload status
    val isUploading: Boolean = false,
    val selectedAppointmentId: String? = null,

    // Polling / Active Analysis
    val pollingReportId: String? = null,
    val pollingReport: ReportAnalysis? = null,
    val isPolling: Boolean = false,

    // Selected Report for displaying details
    val selectedReport: ReportAnalysis? = null
)

sealed class ReportAnalysisEvent {
    object LoadData : ReportAnalysisEvent()
    data class UploadReport(
        val fileBytes: ByteArray,
        val fileName: String,
        val patientId: String
    ) : ReportAnalysisEvent()
    data class TriggerAnalysis(val reportId: String) : ReportAnalysisEvent()
    data class SelectAppointment(val appointmentId: String?) : ReportAnalysisEvent()
    data class ViewReportDetails(val report: ReportAnalysis?) : ReportAnalysisEvent()
    object DismissError : ReportAnalysisEvent()
}

sealed class ReportAnalysisEffect {
    data class ShowSnackbar(val message: String) : ReportAnalysisEffect()
}
