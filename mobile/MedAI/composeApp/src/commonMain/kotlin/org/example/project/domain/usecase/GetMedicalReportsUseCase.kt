package org.example.project.domain.usecase

import org.example.project.domain.model.MedicalReport
import org.example.project.domain.repository.MedicalReportsRepository

class GetMedicalReportsUseCase(
    private val repository: MedicalReportsRepository
) {
    suspend operator fun invoke(patientId: Int, token: String): Result<List<MedicalReport>> {
        return repository.getReports(patientId, token)
    }
}
