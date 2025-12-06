package org.example.project.domain.repository

import org.example.project.domain.model.Specialty

interface SpecialtiesRepository {
    suspend fun getSpecialties(): Result<List<Specialty>>
}
