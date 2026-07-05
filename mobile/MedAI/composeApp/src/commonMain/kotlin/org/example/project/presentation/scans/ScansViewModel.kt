package org.example.project.presentation.scans

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.usecase.scans.*

class ScansViewModel(
    private val patientIdArg: String?,
    private val sessionManager: UserSessionManager,
    private val getScansUseCase: GetScansUseCase,
    private val getScanDetailsUseCase: GetScanDetailsUseCase,
    private val uploadScanUseCase: UploadScanUseCase,
    private val deleteScanUseCase: DeleteScanUseCase,
    private val getScanReportsUseCase: GetScanReportsUseCase,
    private val uploadReportUseCase: UploadReportUseCase,
    private val deleteReportUseCase: DeleteReportUseCase,
    private val getScanImageFileUseCase: GetScanImageFileUseCase,
    private val getReportFileUseCase: GetReportFileUseCase
) : MviScreenModel<ScansState, ScansEvent, ScansEffect>(
    initialState = ScansState()
) {

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
            onEvent(ScansEvent.LoadScans)
        }
    }

    override fun onEvent(event: ScansEvent) {
        when (event) {
            ScansEvent.LoadScans -> loadScans()
            is ScansEvent.ToggleScanExpanded -> toggleScanExpanded(event.scanId, event.patientUserId)
            is ScansEvent.UploadScan -> uploadScan(event.patientUserId, event.appointmentId, event.images)
            is ScansEvent.DeleteScan -> deleteScan(event.patientUserId, event.scanId)
            is ScansEvent.UploadReport -> uploadReport(event.patientUserId, event.scanId, event.fileBytes, event.fileName)
            is ScansEvent.DeleteReport -> deleteReport(event.patientUserId, event.reportId)
            is ScansEvent.FetchScanImage -> fetchScanImage(event.patientUserId, event.imageId, event.imageName)
            is ScansEvent.FetchReportFile -> fetchReportFile(event.patientUserId, event.reportId, event.reportName)
            ScansEvent.CloseFileViewer -> closeFileViewer()
        }
    }

    private fun loadScans() {
        screenModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getScansUseCase().fold(
                onSuccess = { allScans ->
                    // Determine which patient's scans we should view
                    val targetPatientId = patientIdArg ?: state.value.currentUserId

                    // Filter scans to only show those belonging to the target patient
                    val filteredScans = if (targetPatientId != null) {
                        allScans.filter { it.patientUserId == targetPatientId }
                    } else {
                        allScans
                    }

                    setState {
                        copy(
                            isLoading = false,
                            scans = filteredScans
                        )
                    }
                },
                onFailure = { throwable ->
                    setState {
                        copy(
                            isLoading = false,
                            error = throwable.message ?: "Failed to load scans"
                        )
                    }
                    sendEffect(ScansEffect.ShowSnackbar("Error loading scans: ${throwable.message}"))
                }
            )
        }
    }

    private fun toggleScanExpanded(scanId: String, patientUserId: String) {
        val currentlyExpanded = state.value.expandedScanIds
        if (currentlyExpanded.contains(scanId)) {
            setState { copy(expandedScanIds = currentlyExpanded - scanId) }
        } else {
            setState { copy(expandedScanIds = currentlyExpanded + scanId) }
            loadReportsForScan(scanId, patientUserId)
        }
    }

    private fun loadReportsForScan(scanId: String, patientUserId: String) {
        screenModelScope.launch {
            setState {
                copy(
                    reportsLoading = reportsLoading + (scanId to true)
                )
            }
            getScanReportsUseCase(patientUserId, scanId).fold(
                onSuccess = { reportsList ->
                    setState {
                        copy(
                            reports = reports + (scanId to reportsList),
                            reportsLoading = reportsLoading + (scanId to false)
                        )
                    }
                },
                onFailure = { throwable ->
                    setState {
                        copy(
                            reportsLoading = reportsLoading + (scanId to false)
                        )
                    }
                    sendEffect(ScansEffect.ShowSnackbar("Failed to load reports for scan: ${throwable.message}"))
                }
            )
        }
    }

    private fun uploadScan(patientUserId: String, appointmentId: String?, images: List<ByteArray>) {
        if (images.isEmpty()) {
            sendEffect(ScansEffect.ShowSnackbar("Please select at least one image to upload"))
            return
        }
        screenModelScope.launch {
            setState { copy(isUploadingScan = true) }
            uploadScanUseCase(patientUserId, appointmentId, images).fold(
                onSuccess = {
                    setState { copy(isUploadingScan = false) }
                    sendEffect(ScansEffect.ShowSnackbar("Scan uploaded successfully"))
                    loadScans()
                },
                onFailure = { throwable ->
                    setState { copy(isUploadingScan = false) }
                    sendEffect(ScansEffect.ShowSnackbar("Failed to upload scan: ${throwable.message}"))
                }
            )
        }
    }

    private fun deleteScan(patientUserId: String, scanId: String) {
        screenModelScope.launch {
            deleteScanUseCase(patientUserId, scanId).fold(
                onSuccess = {
                    sendEffect(ScansEffect.ShowSnackbar("Scan deleted successfully"))
                    loadScans()
                },
                onFailure = { throwable ->
                    sendEffect(ScansEffect.ShowSnackbar("Failed to delete scan: ${throwable.message}"))
                }
            )
        }
    }

    private fun uploadReport(patientUserId: String, scanId: String, fileBytes: ByteArray, fileName: String) {
        screenModelScope.launch {
            setState { copy(isUploadingReport = true) }
            uploadReportUseCase(patientUserId, scanId, fileBytes, fileName).fold(
                onSuccess = {
                    setState { copy(isUploadingReport = false) }
                    sendEffect(ScansEffect.ShowSnackbar("Report uploaded successfully"))
                    loadReportsForScan(scanId, patientUserId)
                },
                onFailure = { throwable ->
                    setState { copy(isUploadingReport = false) }
                    sendEffect(ScansEffect.ShowSnackbar("Failed to upload report: ${throwable.message}"))
                }
            )
        }
    }

    private fun deleteReport(patientUserId: String, reportId: String) {
        screenModelScope.launch {
            deleteReportUseCase(patientUserId, reportId).fold(
                onSuccess = {
                    sendEffect(ScansEffect.ShowSnackbar("Report deleted successfully"))
                    // Refresh all expanded scans' reports
                    state.value.expandedScanIds.forEach { scanId ->
                        loadReportsForScan(scanId, patientUserId)
                    }
                },
                onFailure = { throwable ->
                    sendEffect(ScansEffect.ShowSnackbar("Failed to delete report: ${throwable.message}"))
                }
            )
        }
    }

    private fun fetchScanImage(patientUserId: String, imageId: String, imageName: String) {
        screenModelScope.launch {
            setState { copy(isDownloadingFile = true) }
            getScanImageFileUseCase(patientUserId, imageId).fold(
                onSuccess = { bytes ->
                    setState {
                        copy(
                            isDownloadingFile = false,
                            viewingFileBytes = bytes,
                            viewingFileName = imageName,
                            viewingFileType = FileType.IMAGE
                        )
                    }
                },
                onFailure = { throwable ->
                    setState { copy(isDownloadingFile = false) }
                    sendEffect(ScansEffect.ShowSnackbar("Failed to download image file: ${throwable.message}"))
                }
            )
        }
    }

    private fun fetchReportFile(patientUserId: String, reportId: String, reportName: String) {
        screenModelScope.launch {
            setState { copy(isDownloadingFile = true) }
            getReportFileUseCase(patientUserId, reportId).fold(
                onSuccess = { bytes ->
                    val type = if (reportName.endsWith(".pdf", ignoreCase = true)) FileType.PDF else FileType.IMAGE
                    setState {
                        copy(
                            isDownloadingFile = false,
                            viewingFileBytes = bytes,
                            viewingFileName = reportName,
                            viewingFileType = type
                        )
                    }
                },
                onFailure = { throwable ->
                    setState { copy(isDownloadingFile = false) }
                    sendEffect(ScansEffect.ShowSnackbar("Failed to download report file: ${throwable.message}"))
                }
            )
        }
    }

    private fun closeFileViewer() {
        setState {
            copy(
                viewingFileBytes = null,
                viewingFileName = null,
                viewingFileType = null
            )
        }
    }
}
