package org.example.project.presentation.scans

import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.model.scans.Scan
import org.example.project.domain.model.scans.Report

data class ScansState(
    val isLoading: Boolean = false,
    val scans: List<Scan> = emptyList(),
    val expandedScanIds: Set<String> = emptySet(),
    val reports: Map<String, List<Report>> = emptyMap(),
    val reportsLoading: Map<String, Boolean> = emptyMap(),
    val currentUserId: String? = null,
    val currentUserRole: UserRole? = null,
    val error: String? = null,

    // Upload & Download states
    val isUploadingScan: Boolean = false,
    val isUploadingReport: Boolean = false,
    val isDownloadingFile: Boolean = false,

    // File viewing state
    val viewingFileBytes: ByteArray? = null,
    val viewingFileName: String? = null,
    val viewingFileType: FileType? = null
)

enum class FileType {
    IMAGE, PDF
}

sealed class ScansEvent {
    object LoadScans : ScansEvent()
    data class ToggleScanExpanded(val scanId: String, val patientUserId: String) : ScansEvent()
    data class UploadScan(
        val patientUserId: String,
        val appointmentId: String?,
        val images: List<ByteArray>
    ) : ScansEvent()
    data class DeleteScan(val patientUserId: String, val scanId: String) : ScansEvent()
    data class UploadReport(
        val patientUserId: String,
        val scanId: String,
        val fileBytes: ByteArray,
        val fileName: String
    ) : ScansEvent()
    data class DeleteReport(val patientUserId: String, val reportId: String) : ScansEvent()
    data class FetchScanImage(val patientUserId: String, val imageId: String, val imageName: String) : ScansEvent()
    data class FetchReportFile(val patientUserId: String, val reportId: String, val reportName: String) : ScansEvent()
    object CloseFileViewer : ScansEvent()
}

sealed class ScansEffect {
    data class ShowSnackbar(val message: String) : ScansEffect()
    object NavigateBack : ScansEffect()
}
