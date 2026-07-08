package org.example.project.domain.usecase.doctor

import org.example.project.domain.model.doctor.Speciality
import org.example.project.domain.repository.doctor.DoctorRepository

class CreateSpecialityUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(name: String): Result<Speciality> {
        return repository.createSpeciality(name)
    }
}
