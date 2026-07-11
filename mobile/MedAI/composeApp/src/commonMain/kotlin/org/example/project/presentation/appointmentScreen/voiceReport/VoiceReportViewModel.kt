package org.example.project.presentation.appointmentScreen.voiceReport

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.audio.AudioRecorder
import org.example.project.domain.audio.PermissionHandler
import org.example.project.domain.audio.AudioRecordingException
import org.example.project.domain.model.voice_report.VoiceReport
import org.example.project.domain.model.voice_report.VoiceReportStatus
import org.example.project.domain.usecase.voice_report.*

class VoiceReportViewModel(
    private val appointmentId: String,
    private val audioRecorder: AudioRecorder,
    private val permissionHandler: PermissionHandler,
    private val uploadVoiceReportUseCase: UploadVoiceReportUseCase,
    private val getVoiceReportsUseCase: GetVoiceReportsUseCase,
    private val getVoiceReportStatusUseCase: GetVoiceReportStatusUseCase,
    private val deleteVoiceReportUseCase: DeleteVoiceReportUseCase,
    private val pollVoiceReportStatusUseCase: PollVoiceReportStatusUseCase
) : MviScreenModel<VoiceReportState, VoiceReportEvent, VoiceReportEffect>(VoiceReportState()) {

    private var recordingTimerJob: Job? = null
    private var pollingJob: Job? = null
    private var lastRecordedBytes: ByteArray? = null

    init {
        loadReports()
    }

    override fun onEvent(event: VoiceReportEvent) {
        when (event) {
            VoiceReportEvent.StartRecording -> {
                startRecording()
            }
            VoiceReportEvent.StopAndUpload -> {
                stopAndUpload()
            }
            VoiceReportEvent.CancelRecording -> {
                cancelRecording()
            }
            is VoiceReportEvent.DeleteReport -> {
                deleteReport(event.reportId)
            }
            is VoiceReportEvent.ViewReport -> {
                setState { copy(phase = VoiceReportPhase.Success(event.report)) }
            }
            VoiceReportEvent.RetryUpload -> {
                retryUpload()
            }
            VoiceReportEvent.DismissError -> {
                setState { copy(phase = VoiceReportPhase.Idle, error = null) }
            }
            VoiceReportEvent.Refresh -> {
                loadReports()
            }
            VoiceReportEvent.RequestPermission -> {
                sendEffect(VoiceReportEffect.RequestMicrophonePermission)
            }
            is VoiceReportEvent.PermissionResult -> {
                if (event.granted) {
                    setState { copy(permissionDenied = false) }
                    startRecording()
                } else {
                    setState { copy(permissionDenied = true) }
                    sendEffect(VoiceReportEffect.ShowToast("Microphone permission is required to record consultations."))
                }
            }
            is VoiceReportEvent.OnReportClicked -> {
                fetchReportDetailsForSheet(event.report.id)
            }
            VoiceReportEvent.DismissReportDetails -> {
                setState { copy(selectedReportDetails = null) }
            }
        }
    }

    private fun startRecording() {
        if (!permissionHandler.hasMicrophonePermission()) {
            setState { copy(permissionDenied = true) }
            sendEffect(VoiceReportEffect.RequestMicrophonePermission)
            return
        }

        setState { copy(permissionDenied = false) }

        recordingTimerJob?.cancel()
        audioRecorder.release()

        try {
            audioRecorder.startRecording()
            setState { copy(phase = VoiceReportPhase.Recording, recordingDurationSeconds = 0) }

            recordingTimerJob = screenModelScope.launch {
                while (true) {
                    delay(1000)
                    setState { copy(recordingDurationSeconds = recordingDurationSeconds + 1) }
                }
            }
        } catch (e: AudioRecordingException) {
            setState {
                copy(
                    phase = VoiceReportPhase.Error(e.message ?: "Failed to start recording audio.", false)
                )
            }
            sendEffect(VoiceReportEffect.ShowToast(e.message ?: "Failed to start recording audio."))
        }
    }

    private fun stopAndUpload() {
        recordingTimerJob?.cancel()
        recordingTimerJob = null

        try {
            val audioBytes = audioRecorder.stopRecording()
            lastRecordedBytes = audioBytes
            uploadBytes(audioBytes)
        } catch (e: AudioRecordingException) {
            setState {
                copy(
                    phase = VoiceReportPhase.Error(e.message ?: "Failed to stop recording audio properly.", false)
                )
            }
            sendEffect(VoiceReportEffect.ShowToast(e.message ?: "Failed to stop recording audio."))
        }
    }

    private fun uploadBytes(audioBytes: ByteArray) {
        setState { copy(isUploading = true) }
        screenModelScope.launch {
            val fileName = "consultation_${Clock.System.now().toEpochMilliseconds()}.m4a"
            uploadVoiceReportUseCase(appointmentId, audioBytes, fileName).fold(
                onSuccess = { report ->
                    setState { copy(isUploading = false) }
                    startPolling(report.id)
                },
                onFailure = { err ->
                    setState {
                        copy(
                            isUploading = false,
                            phase = VoiceReportPhase.Error(err.message ?: "Upload failed", true)
                        )
                    }
                    sendEffect(VoiceReportEffect.ShowToast(err.message ?: "Upload failed"))
                }
            )
        }
    }

    private fun retryUpload() {
        val bytes = lastRecordedBytes
        if (bytes != null) {
            uploadBytes(bytes)
        } else {
            setState { copy(phase = VoiceReportPhase.Idle) }
        }
    }

    private fun cancelRecording() {
        recordingTimerJob?.cancel()
        recordingTimerJob = null
        audioRecorder.release()
        setState { copy(phase = VoiceReportPhase.Idle, recordingDurationSeconds = 0) }
    }

    private fun deleteReport(reportId: String) {
        screenModelScope.launch {
            deleteVoiceReportUseCase(appointmentId, reportId).fold(
                onSuccess = {
                    sendEffect(VoiceReportEffect.ShowToast("Report deleted successfully"))
                    val currentPhase = state.value.phase
                    if (currentPhase is VoiceReportPhase.Success && currentPhase.report.id == reportId) {
                        setState { copy(phase = VoiceReportPhase.Idle) }
                    } else if (currentPhase is VoiceReportPhase.Processing && currentPhase.reportId == reportId) {
                        pollingJob?.cancel()
                        setState { copy(phase = VoiceReportPhase.Idle) }
                    }
                    loadReports()
                },
                onFailure = { err ->
                    sendEffect(VoiceReportEffect.ShowToast(err.message ?: "Failed to delete report"))
                }
            )
        }
    }

    private fun loadReports() {
        screenModelScope.launch {
            setState { copy(isLoadingReports = true) }
            getVoiceReportsUseCase(appointmentId).fold(
                onSuccess = { list ->
                    setState { copy(isLoadingReports = false, reports = list) }

                    // Only auto-transition phase if we are currently Idle
                    if (state.value.phase is VoiceReportPhase.Idle) {
                        val activeReport = list.find { it.status == VoiceReportStatus.QUEUED || it.status == VoiceReportStatus.PROCESSING }
                        if (activeReport != null) {
                            startPolling(activeReport.id)
                        } else {
                            val completedReport = list.filter { it.status == VoiceReportStatus.COMPLETED }
                                .maxByOrNull { it.createdAt }
                            if (completedReport != null) {
                                fetchAndShowCompletedReport(completedReport.id)
                            }
                        }
                    }
                },
                onFailure = { err ->
                    setState { copy(isLoadingReports = false) }
                    sendEffect(VoiceReportEffect.ShowToast(err.message ?: "Failed to load reports"))
                }
            )
        }
    }

    private fun fetchAndShowCompletedReport(reportId: String) {
        screenModelScope.launch {
            setState { copy(isLoadingReports = true) }
            getVoiceReportStatusUseCase(appointmentId, reportId).fold(
                onSuccess = { fullReport ->
                    setState {
                        copy(
                            isLoadingReports = false,
                            phase = VoiceReportPhase.Success(fullReport)
                        )
                    }
                },
                onFailure = { err ->
                    setState { copy(isLoadingReports = false) }
                    sendEffect(VoiceReportEffect.ShowToast(err.message ?: "Failed to load report details"))
                }
            )
        }
    }

    private fun fetchReportDetailsForSheet(reportId: String) {
        screenModelScope.launch {
            setState { copy(isLoadingReports = true) }
            getVoiceReportStatusUseCase(appointmentId, reportId).fold(
                onSuccess = { fullReport ->
                    setState {
                        copy(
                            isLoadingReports = false,
                            selectedReportDetails = fullReport
                        )
                    }
                },
                onFailure = { err ->
                    setState { copy(isLoadingReports = false) }
                    sendEffect(VoiceReportEffect.ShowToast(err.message ?: "Failed to load report details"))
                }
            )
        }
    }

    private fun startPolling(reportId: String) {
        pollingJob?.cancel()
        setState { copy(phase = VoiceReportPhase.Processing(reportId)) }

        pollingJob = screenModelScope.launch {
            pollVoiceReportStatusUseCase(appointmentId, reportId).collect { result ->
                result.fold(
                    onSuccess = { report ->
                        if (report.status == VoiceReportStatus.COMPLETED) {
                            setState { copy(phase = VoiceReportPhase.Success(report)) }
                            loadReports()
                            pollingJob?.cancel()
                        } else if (report.status == VoiceReportStatus.FAILED) {
                            setState {
                                copy(
                                    phase = VoiceReportPhase.Error(
                                        report.errorMessage ?: "AI pipeline analysis failed",
                                        false
                                    )
                                )
                            }
                            loadReports()
                            pollingJob?.cancel()
                        }
                    },
                    onFailure = { err ->
                        setState {
                            copy(
                                phase = VoiceReportPhase.Error(
                                    err.message ?: "Polling status failed",
                                    true
                                )
                            )
                        }
                        pollingJob?.cancel()
                    }
                )
            }
        }
    }

    override fun onDispose() {
        recordingTimerJob?.cancel()
        pollingJob?.cancel()
        audioRecorder.release()
        super.onDispose()
    }
}
