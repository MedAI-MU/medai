package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.remote.dto.AppointmentDto
import org.example.project.data.remote.dto.CategoryDto
import org.example.project.data.remote.dto.SpecialtyDto
import org.example.project.data.remote.dto.UserResponseDto
import org.example.project.data.remote.mapper.mapCategoryDtoToDomain
import org.example.project.data.remote.mapper.mapCategoryKeyToRes
import org.example.project.data.remote.mapper.mapSpecialtyKeyToRes
import org.example.project.data.remote.mapper.mapStatus
import org.example.project.domain.model.Appointment
import org.example.project.domain.model.Category
import org.example.project.domain.model.Doctor
import org.example.project.domain.model.Specialty
import org.example.project.domain.repository.HomeRepository
import kotlin.coroutines.cancellation.CancellationException

class NetworkHomeRepository(
    private val client: HttpClient
) : HomeRepository {

    // --- 1. Get User Name ---
    override suspend fun getUserName(userId: String): Result<String> {
        return try {
            val response: UserResponseDto = client.get("/user/me").body()
            Result.success(response.name)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 2. Get Categories ---
    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            // GET /categories -> returns List<CategoryDto>
            val response: List<CategoryDto> = client.get("/categories").body()

            // Map DTO -> Domain
            val domainList = response.map { dto ->
                mapCategoryDtoToDomain(dto)
            }
            Result.success(domainList)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 3. Get Upcoming Appointments ---
    override suspend fun getUpcomingAppointments(): Result<List<Appointment>> {
        return try {
            // GET /appointments/my-appointments -> returns List<AppointmentDto>
            val response: List<AppointmentDto> = client.get("/appointments/my-appointments").body()

            val upcoming = response.filter { it.status == "pending" || it.status == "confirmed" }

            // Map DTO -> Domain
            val domainList = upcoming.map { dto ->
                Appointment(
                    id = dto.id.toString(),
                    doctor = Doctor(
                        id = dto.doctor?.id ?: "0",
                        name = dto.doctor?.name ?: "Unknown Doctor",
                        specialty = dto.doctor?.specialty ?: "General",
                        rating = dto.doctor?.rating ?: 0.0,
                        imageUrl = dto.doctor?.imageUrl
                    ),
                    date = parseDate(dto.createdAt.take(10)),
                    time = dto.slot?.startTime ?: "00:00", // using slot start time
                    status = mapStatus(dto.status)
                )
            }
            Result.success(domainList)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 4. Get Specialties ---
    override suspend fun getSpecialties(): Result<List<Specialty>> {
        return try {
            // GET /specialties -> returns List<SpecialtyDto>
            val response: List<SpecialtyDto> = client.get("/specialties").body()

            // Map DTO -> Domain
            val domainList = response.map { dto ->
                Specialty(
                    id = dto.id,
                    title = mapSpecialtyKeyToRes(dto.iconKey), // Maps "cardiology" -> Res.string.spec_cardiology
                    iconName = dto.iconKey
                )
            }
            Result.success(domainList)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun parseDate(dateString: String): LocalDate {
    return try {
        // Expecting ISO format "2023-11-22"
        LocalDate.parse(dateString)
    } catch (e: Exception) {
        e.printStackTrace()
        // Default to a safe date but normally this should be fully handled by the backend schema returning correct dates
        val now = kotlinx.datetime.Clock.System.now()
        val localNow = now.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        LocalDate(localNow.year, localNow.monthNumber, localNow.dayOfMonth)
    }
}
