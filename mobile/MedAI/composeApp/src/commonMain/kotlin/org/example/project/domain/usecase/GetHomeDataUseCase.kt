package org.example.project.domain.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.example.project.domain.model.Appointment
import org.example.project.domain.model.Category
import org.example.project.domain.model.Specialty
import org.example.project.domain.repository.HomeRepository

data class HomeData(
    val userName: String,
    val categories: List<Category>,
    val upcomingAppointments: List<Appointment>,
    val specialties: List<Specialty>
)

class GetHomeDataUseCase(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): Result<HomeData> = coroutineScope {
            // Fetch all data in parallel
            val userDeferred = async { repository.getUserName() }
            val categoriesDeferred = async { repository.getCategories() }
            val appointmentsDeferred = async { repository.getUpcomingAppointments() }
            val specialtiesDeferred = async { repository.getSpecialties() }

            val userResult = userDeferred.await()
            val categoriesResult = categoriesDeferred.await()
            val appointmentsResult = appointmentsDeferred.await()
            val specialtiesResult = specialtiesDeferred.await()

            val homeData = HomeData(
                userName = userResult.getOrDefault("User"),
                categories = categoriesResult.getOrDefault(emptyList()),
                upcomingAppointments = appointmentsResult.getOrDefault(emptyList()),
                specialties = specialtiesResult.getOrDefault(emptyList())
            )

            Result.success(homeData)
    }
}
