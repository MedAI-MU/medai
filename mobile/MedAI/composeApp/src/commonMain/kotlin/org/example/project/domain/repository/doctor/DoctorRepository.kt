package org.example.project.domain.repository.doctor

import kotlinx.datetime.LocalDate
import org.example.project.domain.model.doctor.Doctor
import org.example.project.domain.model.appointment.TimeSlot

interface DoctorRepository {
    suspend fun getDoctors(specialtyId: String? = null): Result<List<Doctor>>

    suspend fun getDoctorById(doctorId: String): Result<Doctor>

    suspend fun getAvailableSlots(doctorId: String, date: LocalDate): Result<List<TimeSlot>>

    suspend fun bookAppointment(
        doctorId: String,
        slotId: String
    ): Result<String>
}
