package org.example.project.domain.repository

import org.example.project.domain.model.Appointment
import org.example.project.domain.model.Category
import org.example.project.domain.model.Specialty

interface HomeRepository {

    suspend fun getCategories(): Result<List<Category>>
    suspend fun getUpcomingAppointments(): Result<List<Appointment>>
    suspend fun getSpecialties(): Result<List<Specialty>>
}
