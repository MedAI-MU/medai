package org.example.project.domain.usecase.home

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.example.project.domain.model.appointment.Appointment
import org.example.project.domain.model.home.Category
import org.example.project.domain.model.specialty.Specialty
import org.example.project.domain.repository.home.HomeRepository
import org.example.project.domain.repository.auth.UserSessionManager

data class HomeData(
    val userName: String,
    val userId: String,
    val categories: List<Category>,
    val upcomingAppointments: List<Appointment>,
    val specialties: List<Specialty>
)

class GetHomeDataUseCase(
    private val repository: HomeRepository,
    private val sessionManager: UserSessionManager
) {
    suspend operator fun invoke(): Result<HomeData> = coroutineScope {
            // Fetch all data in parallel
        val userId = sessionManager.getUserId()
            ?: return@coroutineScope Result.failure(Exception("User not logged in"))

        val userName = sessionManager.getUserName() ?: "User"

        val categoriesDeferred = async { repository.getCategories() }
        val appointmentsDeferred = async { repository.getUpcomingAppointments() }
        val specialtiesDeferred = async { repository.getSpecialties() }

        val categoriesResult = categoriesDeferred.await()
        val appointmentsResult = appointmentsDeferred.await()
        val specialtiesResult = specialtiesDeferred.await()

        val homeData = HomeData(
            userName = userName,
            userId = userId,
            categories = categoriesResult.getOrDefault(emptyList()),
            upcomingAppointments = appointmentsResult.getOrDefault(emptyList()),
            specialties = specialtiesResult.getOrDefault(emptyList())
        )

        Result.success(homeData)
    }
}
