package org.example.project.domain.usecase.doctor

import org.example.project.domain.model.doctor.Speciality
import org.example.project.domain.repository.doctor.DoctorRepository

class GetAllSpecialitiesUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(): Result<List<Speciality>> {
        return repository.getAllSpecialities()
    }
}
