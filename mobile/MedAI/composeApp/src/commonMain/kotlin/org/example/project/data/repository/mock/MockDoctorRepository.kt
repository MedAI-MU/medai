package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.example.project.domain.model.Doctor
import org.example.project.domain.model.TimeSlot
import org.example.project.domain.repository.DoctorRepository

class MockDoctorRepository : DoctorRepository {

    private val allDoctors = listOf(
        Doctor(
            id = "d1",
            name = "Dr. Daniel Rodriguez",
            specialty = "Interventional Cardiologist",
            rating = 4.8,
            imageUrl = "https://randomuser.me/api/portraits/men/32.jpg",
            bio = "Dr. Daniel Rodriguez is a highly skilled Interventional Cardiologist with over 15 years of experience in treating complex heart conditions.",
            reviewCount = 120
        ),
        Doctor(
            id = "d2",
            name = "Dr. Jessica Ramirez",
            specialty = "Electrophysiologist",
            rating = 4.9,
            imageUrl = "https://randomuser.me/api/portraits/women/44.jpg",
            bio = "Dr. Jessica Ramirez specializes in heart rhythm disorders and is known for her compassionate patient care.",
            reviewCount = 98
        ),
        Doctor(
            id = "d3",
            name = "Dr. Michael Chang",
            specialty = "Cardiac Imaging Specialist",
            rating = 4.7,
            imageUrl = "https://randomuser.me/api/portraits/men/86.jpg",
            bio = "Dr. Michael Chang is an expert in advanced cardiac imaging techniques, ensuring accurate diagnoses for his patients.",
            reviewCount = 85
        ),
        Doctor(
            id = "d4",
            name = "Dr. Michael Davidson, M.D.",
            specialty = "Cardiology",
            rating = 4.6,
            imageUrl = "https://randomuser.me/api/portraits/men/11.jpg",
            bio = "Dr. Michael Davidson is a dedicated Cardiologist committed to improving heart health through preventive care.",
            reviewCount = 70
        )
    )

    override suspend fun getDoctors(specialtyId: String?): Result<List<Doctor>> {
        delay(500) // Simulate network
        // In a real mock, you might filter by specialtyId if you had a mapping.
        // For now, returning all doctors is fine for the prototype.
        return Result.success(allDoctors)
    }

    override suspend fun getDoctorById(doctorId: String): Result<Doctor> {
        delay(300)
        val doctor = allDoctors.find { it.id == doctorId }
        return if (doctor != null) {
            Result.success(doctor)
        } else {
            Result.failure(Exception("Doctor not found"))
        }
    }

    override suspend fun getAvailableSlots(doctorId: String, date: LocalDate): Result<List<TimeSlot>> {
        delay(300)
        val isEvenDay = date.dayOfMonth % 2 == 0

        val slots = listOf(
            TimeSlot("s1", "09:00 AM", isAvailable = true),
            TimeSlot("s2", "09:30 AM", isAvailable = isEvenDay),
            TimeSlot("s3", "10:00 AM", isAvailable = false),
            TimeSlot("s4", "10:30 AM", isAvailable = true),
            TimeSlot("s5", "11:00 AM", isAvailable = true),
            TimeSlot("s6", "11:30 AM", isAvailable = isEvenDay),
            TimeSlot("s7", "02:00 PM", isAvailable = true),
            TimeSlot("s8", "02:30 PM", isAvailable = false),
            TimeSlot("s9", "03:00 PM", isAvailable = true),
            TimeSlot("s10", "03:30 PM", isAvailable = true)
        )
        return Result.success(slots)
    }
}
