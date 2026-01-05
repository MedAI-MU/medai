package org.example.project.domain.repository

import kotlinx.datetime.LocalDate
import org.example.project.domain.model.Doctor
import org.example.project.domain.model.TimeSlot

interface DoctorRepository {
    suspend fun getDoctors(specialtyId: String? = null): Result<List<Doctor>>

    suspend fun getDoctorById(doctorId: String): Result<Doctor>

    suspend fun getAvailableSlots(doctorId: String, date: LocalDate): Result<List<TimeSlot>>

    suspend fun bookAppointment(
        doctorId: String,
        slotId: String,
        date: LocalDate,
        patientName: String,
        patientAge: String,
        patientGender: String,
        problemDescription: String
    ): Result<String>
}
