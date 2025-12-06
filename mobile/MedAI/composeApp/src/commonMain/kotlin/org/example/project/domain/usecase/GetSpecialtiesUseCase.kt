package org.example.project.domain.usecase

import org.example.project.domain.model.Specialty
import org.example.project.domain.repository.SpecialtiesRepository

class GetSpecialtiesUseCase(
    private val repository: SpecialtiesRepository
) {
    suspend operator fun invoke(): Result<List<Specialty>> {
        return repository.getSpecialties()
    }
}
