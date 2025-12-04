package org.example.project.domain.repository

import org.example.project.domain.model.Doctor

interface DoctorRepository {
    suspend fun getDoctors(specialtyId: String? = null): Result<List<Doctor>>
}
