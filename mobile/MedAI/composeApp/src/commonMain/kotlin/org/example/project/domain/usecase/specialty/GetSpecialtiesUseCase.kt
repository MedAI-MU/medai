package org.example.project.domain.usecase.specialty

import org.example.project.domain.model.specialty.Specialty
import org.example.project.domain.repository.specialty.SpecialtiesRepository

class GetSpecialtiesUseCase(
    private val repository: SpecialtiesRepository
) {
    suspend operator fun invoke(): Result<List<Specialty>> {
        return repository.getSpecialties()
    }
}
