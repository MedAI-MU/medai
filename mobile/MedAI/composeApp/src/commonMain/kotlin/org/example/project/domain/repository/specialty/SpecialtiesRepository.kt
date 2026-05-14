package org.example.project.domain.repository.specialty

import org.example.project.domain.model.specialty.Specialty

interface SpecialtiesRepository {
    suspend fun getSpecialties(): Result<List<Specialty>>
}
