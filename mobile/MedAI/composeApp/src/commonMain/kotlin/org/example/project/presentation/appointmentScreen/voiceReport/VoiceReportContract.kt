package org.example.project.presentation.appointmentScreen.voiceReport

import org.example.project.domain.model.voice_report.VoiceReport

data class VoiceReportState(
    val reports: List<VoiceReport> = emptyList(),
    val isLoadingReports: Boolean = false,
    val phase: VoiceReportPhase = VoiceReportPhase.Idle,
    val recordingDurationSeconds: Int = 0,
    val isUploading: Boolean = false,
    val permissionDenied: Boolean = false,
    val selectedReportDetails: VoiceReport? = null,
    val error: String? = null
)

sealed interface VoiceReportPhase {
    object Idle : VoiceReportPhase
    object Recording : VoiceReportPhase
    data class Processing(val reportId: String) : VoiceReportPhase
    data class Success(val report: VoiceReport) : VoiceReportPhase
    data class Error(val message: String, val isRetryable: Boolean) : VoiceReportPhase
}

sealed interface VoiceReportEvent {
    object StartRecording : VoiceReportEvent
    object StopAndUpload : VoiceReportEvent
    object CancelRecording : VoiceReportEvent
    data class DeleteReport(val reportId: String) : VoiceReportEvent
    data class ViewReport(val report: VoiceReport) : VoiceReportEvent
    object RetryUpload : VoiceReportEvent
    object DismissError : VoiceReportEvent
    object Refresh : VoiceReportEvent
    object RequestPermission : VoiceReportEvent
    data class PermissionResult(val granted: Boolean) : VoiceReportEvent
    data class OnReportClicked(val report: VoiceReport) : VoiceReportEvent
    object DismissReportDetails : VoiceReportEvent
}

sealed interface VoiceReportEffect {
    data class ShowToast(val message: String) : VoiceReportEffect
    object RequestMicrophonePermission : VoiceReportEffect
}
