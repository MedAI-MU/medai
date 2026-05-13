package org.example.project.domain.usecase.doctor

import org.example.project.domain.model.doctor.Doctor
import org.example.project.domain.repository.doctor.DoctorRepository

class GetDoctorsUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(specialtyId: String? = null): Result<List<Doctor>> {
        return repository.getDoctors(specialtyId)
    }
}
