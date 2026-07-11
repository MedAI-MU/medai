package org.example.project.data.remote.mapper

import org.example.project.data.remote.dto.voice_report.VoiceReportResponseDto
import org.example.project.data.remote.dto.voice_report.ClinicalReportDto
import org.example.project.data.remote.dto.voice_report.ClinicalSymptomDto
import org.example.project.domain.model.voice_report.VoiceReport
import org.example.project.domain.model.voice_report.VoiceReportStatus
import org.example.project.domain.model.voice_report.ClinicalReport
import org.example.project.domain.model.voice_report.ClinicalSymptom

fun String.toVoiceReportStatus(): VoiceReportStatus {
    return when (this.lowercase()) {
        "queued" -> VoiceReportStatus.QUEUED
        "processing" -> VoiceReportStatus.PROCESSING
        "completed" -> VoiceReportStatus.COMPLETED
        "failed" -> VoiceReportStatus.FAILED
        else -> VoiceReportStatus.FAILED
    }
}

fun VoiceReportResponseDto.toDomain(): VoiceReport {
    return VoiceReport(
        id = this.id.toString(),
        status = this.status.toVoiceReportStatus(),
        patientUserId = this.patientUserId.toString(),
        doctorUserId = this.doctorUserId.toString(),
        appointmentId = this.appointmentId?.toString(),
        transcription = this.transcription,
        clinicalReport = this.clinicalReport?.toDomain(),
        audioUrl = this.audioUrl,
        errorMessage = this.errorMessage,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun ClinicalReportDto.toDomain(): ClinicalReport {
    return ClinicalReport(
        symptoms = this.symptoms.map { it.toDomain() },
        systematicSummary = this.systemic_summary
    )
}

fun ClinicalSymptomDto.toDomain(): ClinicalSymptom {
    return ClinicalSymptom(
        descriptionAr = this.description_ar,
        clinicalTerm = this.clinical_term
    )
}
