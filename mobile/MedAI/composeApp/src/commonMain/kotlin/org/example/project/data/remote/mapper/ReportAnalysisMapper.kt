package org.example.project.data.remote.mapper

import org.example.project.data.remote.dto.report_analysis.*
import org.example.project.domain.model.report_analysis.*

fun ReportAnalysisResponseDto.toDomain(): ReportAnalysis {
    return ReportAnalysis(
        id = this.id.toString(),
        patientUserId = this.patientUserId.toString(),
        scanId = this.scanId?.toString(),
        appointmentId = this.appointmentId?.toString(),
        path = this.path,
        analysisStatus = this.analysisStatus?.toReportAnalysisStatus(),
        analysisResult = this.analysisResult?.toDomain(),
        analysisError = this.analysisError,
        createdAt = this.createdAt
    )
}

fun AnalysisResultDto.toDomain(): AnalysisResult {
    return AnalysisResult(
        patientMetadata = this.patient_metadata?.toDomain(),
        keyFindings = this.key_findings.map { it.toDomain() },
        overallSummaryAr = this.overall_summary_ar,
        safetyNoteAr = this.safety_note_ar
    )
}

fun PatientMetadataDto.toDomain(): PatientMetadata {
    return PatientMetadata(
        name = this.name,
        age = this.age,
        gender = this.gender,
        reportDate = this.report_date
    )
}

fun KeyFindingDto.toDomain(): KeyFinding {
    return KeyFinding(
        testName = this.test_name,
        measuredValue = this.measured_value,
        unit = this.unit,
        referenceRange = this.reference_range,
        statusAr = this.status_ar,
        patientExplanationAr = this.patient_explanation_ar
    )
}

fun String.toReportAnalysisStatus(): ReportAnalysisStatus? {
    return when (this.lowercase()) {
        "queued" -> ReportAnalysisStatus.QUEUED
        "processing" -> ReportAnalysisStatus.PROCESSING
        "completed" -> ReportAnalysisStatus.COMPLETED
        "failed" -> ReportAnalysisStatus.FAILED
        else -> null
    }
}
