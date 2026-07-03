package org.example.project.domain.usecase.doctor

import org.example.project.domain.repository.doctor.DoctorRepository

class RemoveSpecialityUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(doctorId: String, doctorSpecialityId: Int): Result<Unit> {
        return repository.removeSpeciality(doctorId, doctorSpecialityId)
    }
}
