package org.example.project.domain.usecase

import org.example.project.domain.model.CreateMedicalReportRequest
import org.example.project.domain.model.MedicalReport
import org.example.project.domain.repository.MedicalReportsRepository

class CreateMedicalReportUseCase(
    private val repository: MedicalReportsRepository
) {
    suspend operator fun invoke(patientId: Int, token: String, request: CreateMedicalReportRequest): Result<MedicalReport> {
        return repository.createReport(patientId, token, request)
    }
}
