package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.spec_cardiology
import medai.composeapp.generated.resources.spec_dermatology
import medai.composeapp.generated.resources.spec_general
import medai.composeapp.generated.resources.spec_gynecology
import medai.composeapp.generated.resources.spec_odontology
import medai.composeapp.generated.resources.spec_oncology
import org.example.project.domain.model.specialty.Specialty
import org.example.project.domain.repository.specialty.SpecialtiesRepository

class MockSpecialtiesRepository : SpecialtiesRepository {

    override suspend fun getSpecialties(): Result<List<Specialty>> {
        // Simulate network delay
        delay(800)

        val mockList = listOf(
            Specialty(
                id = "1",
                title = Res.string.spec_cardiology,
                iconName = "cardiology"
            ),
            Specialty(
                id = "2",
                title = Res.string.spec_dermatology,
                iconName = "dermatology"
            ),
            Specialty(
                id = "3",
                title = Res.string.spec_general,
                iconName = "general"
            ),
            Specialty(
                id = "4",
                title = Res.string.spec_gynecology,
                iconName = "gynecology"
            ),
            Specialty(
                id = "5",
                title = Res.string.spec_odontology,
                iconName = "odontology"
            ),
            Specialty(
                id = "6",
                title = Res.string.spec_oncology,
                iconName = "oncology"
            ),
            Specialty(
                id = "7",
                title = Res.string.spec_cardiology,
                iconName = "ophthalmology"
            ),
            Specialty(
                id = "8",
                title = Res.string.spec_general,
                iconName = "orthopedics"
            )
        )

        return Result.success(mockList)
    }
}
