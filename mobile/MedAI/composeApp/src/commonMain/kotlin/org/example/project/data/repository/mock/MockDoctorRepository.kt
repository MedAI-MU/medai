package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import org.example.project.domain.model.Doctor
import org.example.project.domain.repository.DoctorRepository

class MockDoctorRepository : DoctorRepository {

    override suspend fun getDoctors(specialtyId: String?): Result<List<Doctor>> {
        delay(500) // Simulate network

        // Mock data
        val allDoctors = listOf(
            Doctor(
                id = "d1",
                name = "Dr. Moahmed El Dahan",
                specialty = "Interventional Cardiologist",
                rating = 4.8,
                imageUrl = "https://randomuser.me/api/portraits/men/32.jpg"
            ),
            Doctor(
                id = "d2",
                name = "Dr. Mostafa Atef",
                specialty = "Electrophysiologist",
                rating = 4.9,
                imageUrl = "https://randomuser.me/api/portraits/women/44.jpg"
            ),
            Doctor(
                id = "d3",
                name = "Dr. Ahmed Goda",
                specialty = "Cardiac Imaging Specialist",
                rating = 4.7,
                imageUrl = "https://randomuser.me/api/portraits/men/86.jpg"
            ),
            Doctor(
                id = "d4",
                name = "Dr. Yossef Elsherbiny, M.D.",
                specialty = "Cardiology",
                rating = 4.6,
                imageUrl = "https://randomuser.me/api/portraits/men/11.jpg"
            )
        )

        return Result.success(allDoctors)
    }
}
