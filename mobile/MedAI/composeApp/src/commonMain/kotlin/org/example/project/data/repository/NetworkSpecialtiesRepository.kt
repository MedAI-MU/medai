package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.example.project.data.remote.dto.SpecialtyDto
import org.example.project.data.remote.mapper.mapSpecialtyKeyToRes
import org.example.project.domain.model.specialty.Specialty
import org.example.project.domain.repository.specialty.SpecialtiesRepository

class NetworkSpecialtiesRepository(
    private val client: HttpClient
) : SpecialtiesRepository {

    override suspend fun getSpecialties(): Result<List<Specialty>> {
        return try {
            // GET /specialties -> returns List<SpecialtyDto>
            val response: List<SpecialtyDto> = client.get("/specialties").body()

            val domainList = response.map { dto ->
                Specialty(
                    id = dto.id,
                    title = mapSpecialtyKeyToRes(dto.iconKey),
                    iconName = dto.iconKey
                )
            }
            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
