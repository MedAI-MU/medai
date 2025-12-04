package org.example.project.domain.usecase

import org.example.project.domain.model.Doctor
import org.example.project.domain.repository.DoctorRepository

class GetDoctorDetailsUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(doctorId: String): Result<Doctor> {
        return repository.getDoctorById(doctorId)
    }
}
