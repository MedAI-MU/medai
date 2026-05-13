package org.example.project.domain.usecase.doctor

import org.example.project.domain.model.doctor.Doctor
import org.example.project.domain.repository.doctor.DoctorRepository

class GetDoctorDetailsUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(doctorId: String): Result<Doctor> {
        return repository.getDoctorById(doctorId)
    }
}
