package org.example.project.domain.model.report_analysis

enum class ReportAnalysisStatus {
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED;

    fun isTerminal(): Boolean = this == COMPLETED || this == FAILED
}

data class PatientMetadata(
    val name: String?,
    val age: String?,
    val gender: String?,
    val reportDate: String?
)

data class KeyFinding(
    val testName: String,
    val measuredValue: String,
    val unit: String,
    val referenceRange: String,
    val statusAr: String,
    val patientExplanationAr: String
)

data class AnalysisResult(
    val patientMetadata: PatientMetadata?,
    val keyFindings: List<KeyFinding>,
    val overallSummaryAr: String?,
    val safetyNoteAr: String?
)

data class ReportAnalysis(
    val id: String,
    val patientUserId: String,
    val scanId: String?,
    val appointmentId: String?,
    val path: String,
    val analysisStatus: ReportAnalysisStatus?,
    val analysisResult: AnalysisResult?,
    val analysisError: String?,
    val createdAt: String
)
