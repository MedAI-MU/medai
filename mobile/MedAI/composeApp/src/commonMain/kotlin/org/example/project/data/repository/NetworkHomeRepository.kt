package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.cat_doctors
import medai.composeapp.generated.resources.cat_favorite
import medai.composeapp.generated.resources.cat_record
import medai.composeapp.generated.resources.cat_specialties
import medai.composeapp.generated.resources.spec_cardiology
import medai.composeapp.generated.resources.spec_dermatology
import medai.composeapp.generated.resources.spec_general
import medai.composeapp.generated.resources.spec_gynecology
import medai.composeapp.generated.resources.spec_odontology
import medai.composeapp.generated.resources.spec_oncology
import org.example.project.core.presentation.util.UiText
import org.example.project.data.remote.dto.appointment.AppointmentResponseDto
import org.example.project.data.remote.dto.CategoryDto
import org.example.project.data.remote.dto.SpecialtyDto
import org.example.project.data.remote.dto.doctor.SpecialityItemResponseDto

import org.example.project.data.remote.mapper.mapCategoryDtoToDomain
import org.example.project.data.remote.mapper.mapCategoryKeyToRes
import org.example.project.data.remote.mapper.mapSpecialtyKeyToRes
import org.example.project.data.remote.mapper.mapStatus
import org.example.project.design_system.icons.MedAIIcons
import org.example.project.domain.model.appointment.Appointment
import org.example.project.domain.model.home.Category
import org.example.project.domain.model.home.CategoryType
import org.example.project.domain.model.doctor.Doctor
import org.example.project.domain.model.specialty.Specialty
import org.example.project.domain.repository.home.HomeRepository
import kotlin.coroutines.cancellation.CancellationException

class NetworkHomeRepository(
    private val client: HttpClient
) : HomeRepository {

    // --- 2. Get Categories ---
    override suspend fun getCategories(): Result<List<Category>> {

        return Result.success(
            listOf(
                Category("1", Res.string.cat_favorite, CategoryType.FAVORITE, MedAIIcons.Favorites),
                Category("2", Res.string.cat_doctors,CategoryType.DOCTORS,  MedAIIcons.Doctor),
                Category("4", Res.string.cat_specialties,CategoryType.SPECIALTIES,  MedAIIcons.Specialties),
                Category("5", Res.string.cat_record,CategoryType.RECORDS,  MedAIIcons.Record),
            )
        )

//        return try {
//            // GET /categories -> returns List<CategoryDto>
//            val response: List<CategoryDto> = client.get("/categories").body()
//
//            // Map DTO -> Domain
//            val domainList = response.map { dto ->
//                mapCategoryDtoToDomain(dto)
//            }
//            Result.success(domainList)
//        } catch (e: CancellationException) {
//            throw e
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
    }

    // --- 3. Get Upcoming Appointments ---
    override suspend fun getUpcomingAppointments(): Result<List<Appointment>> {
        return try {
            // GET appointments/me -> returns List<AppointmentResponseDto>
            val response: List<AppointmentResponseDto> = client.get("appointments/me").body()

            val upcoming = response.filter { it.status == "pending" || it.status == "confirmed" }

            // Map DTO -> Domain
            // Note: Backend AppointmentResponseDto doesn't include nested doctor/scheduleSlot objects.
            // We use placeholder values here; the full doctor info would require a separate API call.
            val domainList = upcoming.map { dto ->
                Appointment(
                    id = dto.id.toString(),
                    doctor = Doctor(
                        id = dto.doctorUserId.toString(),
                        name = "Doctor #${dto.doctorUserId}",
                        specialty = "General",
                        rating = 0.0,
                        imageUrl = null
                    ),
                    date = LocalDate.parse(
                        dto.scheduleSlot?.schedule?.dayDate ?: dto.createdAt.take(10)
                    ),
                    time = dto.scheduleSlot?.startTime?.take(5) ?: "00:00",
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
            val response: List<SpecialityItemResponseDto> = client.get("doctors/specialities").body()

            val domainList = response.map { dto ->
                Specialty(
                    id = dto.name,
                    title = UiText.DynamicString(dto.name),
                    iconName = dto.name
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
