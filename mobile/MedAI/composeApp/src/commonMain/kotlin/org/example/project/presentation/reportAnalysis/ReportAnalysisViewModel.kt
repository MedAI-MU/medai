package org.example.project.presentation.reportAnalysis

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.repository.appointment.AppointmentRepository
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.usecase.report_analysis.*

class ReportAnalysisViewModel(
    private val patientIdArg: String?,
    private val sessionManager: UserSessionManager,
    private val appointmentRepository: AppointmentRepository,
    private val uploadReportUseCase: UploadReportUseCase,
    private val triggerAnalysisUseCase: TriggerAnalysisUseCase,
    private val pollAnalysisStatusUseCase: PollAnalysisStatusUseCase,
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val getAnalysisUseCase: GetAnalysisUseCase
) : MviScreenModel<ReportAnalysisState, ReportAnalysisEvent, ReportAnalysisEffect>(
    initialState = ReportAnalysisState()
) {

    private var pollingJob: kotlinx.coroutines.Job? = null

    init {
        screenModelScope.launch {
            val userId = sessionManager.getUserId()
            val userRole = sessionManager.getUserRole()
            setState {
                copy(
                    currentUserId = userId,
                    currentUserRole = userRole
                )
            }
            onEvent(ReportAnalysisEvent.LoadData)
        }
    }

    override fun onEvent(event: ReportAnalysisEvent) {
        when (event) {
            ReportAnalysisEvent.LoadData -> loadData()
            is ReportAnalysisEvent.UploadReport -> uploadReport(event.fileBytes, event.fileName, event.patientId)
            is ReportAnalysisEvent.TriggerAnalysis -> triggerAnalysis(event.reportId)
            is ReportAnalysisEvent.SelectAppointment -> setState { copy(selectedAppointmentId = event.appointmentId) }
            is ReportAnalysisEvent.ViewReportDetails -> setState { copy(selectedReport = event.report) }
            ReportAnalysisEvent.DismissError -> setState { copy(error = null) }
        }
    }

    private fun loadData() {
        screenModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val targetPatientId = patientIdArg ?: state.value.currentUserId ?: ""

            // 1. Fetch reports
            val reportsResult = getAllReportsUseCase()
            // 2. Fetch appointments for linking
            val appointmentsResult = appointmentRepository.getMyAppointments()

            reportsResult.fold(
                onSuccess = { reportsList ->
                    val filteredReports = if (patientIdArg != null) {
                        reportsList.filter { it.patientUserId == targetPatientId }
                    } else {
                        reportsList
                    }

                    appointmentsResult.fold(
                        onSuccess = { appointmentsList ->
                            val filteredAppointments = appointmentsList
                                .filter { it.patientId == targetPatientId && it.status != AppointmentDetailStatus.CANCELLED }
                                .sortedWith(compareBy<AppointmentDetail> {
                                    if (it.status == AppointmentDetailStatus.FINISHED) 0 else 1
                                }.thenByDescending { it.date })

                            setState {
                                copy(
                                    isLoading = false,
                                    reports = filteredReports,
                                    appointments = filteredAppointments
                                )
                            }
                        },
                        onFailure = {
                            setState {
                                copy(
                                    isLoading = false,
                                    reports = filteredReports
                                )
                            }
                        }
                    )
                },
                onFailure = { throwable ->
                    setState {
                        copy(
                            isLoading = false,
                            error = throwable.message ?: "Failed to load reports"
                        )
                    }
                }
            )
        }
    }

    private fun uploadReport(fileBytes: ByteArray, fileName: String, patientId: String) {
        screenModelScope.launch {
            setState { copy(isUploading = true, error = null) }
            uploadReportUseCase(
                patientUserId = patientId,
                fileBytes = fileBytes,
                fileName = fileName,
                appointmentId = state.value.selectedAppointmentId
            ).fold(
                onSuccess = { uploadedReport ->
                    setState {
                        copy(
                            isUploading = false,
                            reports = listOf(uploadedReport) + reports
                        )
                    }
                    sendEffect(ReportAnalysisEffect.ShowSnackbar("Report uploaded successfully! Initiating AI Analysis..."))
                    // Automatically trigger analysis for this new report
                    triggerAnalysis(uploadedReport.id)
                },
                onFailure = { throwable ->
                    setState {
                        copy(
                            isUploading = false,
                            error = throwable.message ?: "Failed to upload report"
                        )
                    }
                    sendEffect(ReportAnalysisEffect.ShowSnackbar("Upload failed: ${throwable.message}"))
                }
            )
        }
    }

    private fun triggerAnalysis(reportId: String) {
        screenModelScope.launch {
            triggerAnalysisUseCase(reportId).fold(
                onSuccess = { report ->
                    // Update report in the list
                    setState {
                        copy(
                            reports = reports.map { if (it.id == report.id) report else it }
                        )
                    }
                    startPolling(reportId)
                },
                onFailure = { throwable ->
                    sendEffect(ReportAnalysisEffect.ShowSnackbar("Failed to start analysis: ${throwable.message}"))
                }
            )
        }
    }

    private fun startPolling(reportId: String) {
        pollingJob?.cancel()
        pollingJob = screenModelScope.launch {
            setState {
                copy(
                    pollingReportId = reportId,
                    isPolling = true
                )
            }
            pollAnalysisStatusUseCase(reportId).collectLatest { result ->
                result.fold(
                    onSuccess = { report ->
                        setState {
                            copy(
                                pollingReport = report,
                                reports = reports.map { if (it.id == report.id) report else it }
                            )
                        }
                        if (report.analysisStatus?.isTerminal() == true) {
                            setState {
                                copy(
                                    isPolling = false,
                                    pollingReportId = null,
                                    pollingReport = null
                                )
                            }
                            if (report.analysisStatus == org.example.project.domain.model.report_analysis.ReportAnalysisStatus.COMPLETED) {
                                sendEffect(ReportAnalysisEffect.ShowSnackbar("AI Report analysis completed!"))
                                setState { copy(selectedReport = report) }
                            } else {
                                sendEffect(ReportAnalysisEffect.ShowSnackbar("AI analysis failed: ${report.analysisError}"))
                            }
                            pollingJob?.cancel()
                        }
                    },
                    onFailure = { throwable ->
                        setState {
                            copy(
                                isPolling = false,
                                pollingReportId = null,
                                pollingReport = null
                            )
                        }
                        sendEffect(ReportAnalysisEffect.ShowSnackbar("Error polling analysis: ${throwable.message}"))
                        pollingJob?.cancel()
                    }
                )
            }
        }
    }

    override fun onDispose() {
        pollingJob?.cancel()
        super.onDispose()
    }
}
