package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.cat_doctors
import medai.composeapp.generated.resources.cat_favorite
import medai.composeapp.generated.resources.cat_pharmacy
import medai.composeapp.generated.resources.cat_record
import medai.composeapp.generated.resources.cat_specialties
import medai.composeapp.generated.resources.spec_cardiology
import medai.composeapp.generated.resources.spec_dermatology
import medai.composeapp.generated.resources.spec_general
import medai.composeapp.generated.resources.spec_gynecology
import medai.composeapp.generated.resources.spec_odontology
import medai.composeapp.generated.resources.spec_oncology
import org.example.project.design_system.icons.MedAIIcons
import org.example.project.domain.model.Appointment
import org.example.project.domain.model.AppointmentStatus
import org.example.project.domain.model.Category
import org.example.project.domain.model.CategoryType
import org.example.project.domain.model.Doctor
import org.example.project.domain.model.Specialty
import org.example.project.domain.repository.HomeRepository

class MockHomeRepository : HomeRepository {

//    override suspend fun getUserName(
//        userId: String
//    ): Result<String> {
//        delay(500)
//        return Result.success("Jane Doe")
//    }

    override suspend fun getCategories(): Result<List<Category>> {
        // No delay, static data
        return Result.success(
            listOf(
                Category("1", Res.string.cat_favorite, CategoryType.FAVORITE, MedAIIcons.Favorites),
                Category("2", Res.string.cat_doctors,CategoryType.DOCTORS,  MedAIIcons.Doctor),
                Category("4", Res.string.cat_specialties,CategoryType.SPECIALTIES,  MedAIIcons.Specialties),
                Category("5", Res.string.cat_record,CategoryType.RECORDS,  MedAIIcons.Record),
            )
        )
    }

    override suspend fun getUpcomingAppointments(): Result<List<Appointment>> {
        delay(1500) // Simulate network
        return Result.success(
            listOf(
                Appointment(
                    id = "1",
                    doctor = Doctor("d1", "Dr. Olivia Turner", "Dermatologist"),
                    date = LocalDate(2025, 11, 11),
                    time = "10:00 am",
                    status = AppointmentStatus.Pending
                ),
                Appointment(
                    id = "2",
                    doctor = Doctor("d2", "Dr. Alexander Bennett", "Dermatologist"),
                    date = LocalDate(2025, 11, 30),
                    time = "08:00 am",
                    status = AppointmentStatus.Finished
                ),
                Appointment(
                    id = "1",
                    doctor = Doctor("d1", "Dr. Olivia Turner", "Dermatologist"),
                    date = LocalDate(2025, 12, 3),
                    time = "10:00 am",
                    status = AppointmentStatus.Pending
                ),
                Appointment(
                    id = "2",
                    doctor = Doctor("d2", "Dr. Alexander Bennett", "Dermatologist"),
                    date = LocalDate(2025, 12, 3),
                    time = "08:00 am",
                    status = AppointmentStatus.Finished
                ),
                Appointment(
                    id = "1",
                    doctor = Doctor("d1", "Dr. Olivia Turner", "Dermatologist"),
                    date = LocalDate(2025, 12, 5),
                    time = "10:00 am",
                    status = AppointmentStatus.Pending
                ),
                Appointment(
                    id = "2",
                    doctor = Doctor("d2", "Dr. Alexander Bennett", "Dermatologist"),
                    date = LocalDate(2025, 12, 11),
                    time = "08:00 am",
                    status = AppointmentStatus.Finished
                )
            )
        )
    }

    override suspend fun getSpecialties(): Result<List<Specialty>> {
        delay(1000)
        return Result.success(
            listOf(
                Specialty("1", Res.string.spec_cardiology, "cardiology"),
                Specialty("2", Res.string.spec_dermatology, "dermatology"),
                Specialty("3", Res.string.spec_general, "general"),
                Specialty("4", Res.string.spec_gynecology, "gynecology"),
                Specialty("5", Res.string.spec_odontology, "odontology"),
                Specialty("6", Res.string.spec_oncology, "oncology"),
            )
        )
    }
}
