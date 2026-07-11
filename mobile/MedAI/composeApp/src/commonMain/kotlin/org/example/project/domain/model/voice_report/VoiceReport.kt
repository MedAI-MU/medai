package org.example.project.domain.model.voice_report

enum class VoiceReportStatus {
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED;

    fun isTerminal(): Boolean = this == COMPLETED || this == FAILED
}

data class ClinicalSymptom(
    val descriptionAr: String,
    val clinicalTerm: String
)

data class ClinicalReport(
    val symptoms: List<ClinicalSymptom>,
    val systematicSummary: String
)

data class VoiceReport(
    val id: String,
    val status: VoiceReportStatus,
    val patientUserId: String,
    val doctorUserId: String,
    val appointmentId: String?,
    val transcription: String?,
    val clinicalReport: ClinicalReport?,
    val audioUrl: String?,
    val errorMessage: String?,
    val createdAt: String,
    val updatedAt: String?
)
