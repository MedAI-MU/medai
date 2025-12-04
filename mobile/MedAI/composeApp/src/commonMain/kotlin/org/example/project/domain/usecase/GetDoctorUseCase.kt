package org.example.project.domain.usecase

import org.example.project.domain.model.Doctor
import org.example.project.domain.repository.DoctorRepository

class GetDoctorsUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(specialtyId: String? = null): Result<List<Doctor>> {
        return repository.getDoctors(specialtyId)
    }
}
