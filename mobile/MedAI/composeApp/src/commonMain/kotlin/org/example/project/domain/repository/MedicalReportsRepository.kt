package org.example.project.domain.repository

import org.example.project.domain.model.MedicalReport
import org.example.project.domain.model.CreateMedicalReportRequest

interface MedicalReportsRepository {
    suspend fun getReports(patientId: Int, token: String): Result<List<MedicalReport>>
    suspend fun createReport(patientId: Int, token: String, request: CreateMedicalReportRequest): Result<MedicalReport>
}
