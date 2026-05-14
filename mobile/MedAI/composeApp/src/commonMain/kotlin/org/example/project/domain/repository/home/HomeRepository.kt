package org.example.project.domain.repository.home

import org.example.project.domain.model.appointment.Appointment
import org.example.project.domain.model.home.Category
import org.example.project.domain.model.specialty.Specialty

interface HomeRepository {

    suspend fun getCategories(): Result<List<Category>>
    suspend fun getUpcomingAppointments(): Result<List<Appointment>>
    suspend fun getSpecialties(): Result<List<Specialty>>
}
