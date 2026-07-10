package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.example.project.core.presentation.util.UiText
import org.example.project.data.remote.dto.doctor.SpecialityItemResponseDto
import org.example.project.domain.model.specialty.Specialty
import org.example.project.domain.repository.specialty.SpecialtiesRepository

class NetworkSpecialtiesRepository(
    private val client: HttpClient
) : SpecialtiesRepository {

    override suspend fun getSpecialties(): Result<List<Specialty>> {
        return try {
            // GET doctors/specialities -> returns List<SpecialityItemResponseDto>
            val response: List<SpecialityItemResponseDto> = client.get("doctors/specialities").body()

            val domainList = response.map { dto ->
                Specialty(
                    id = dto.name,
                    title = UiText.DynamicString(dto.name),
                    iconName = dto.name
                )
            }
            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
