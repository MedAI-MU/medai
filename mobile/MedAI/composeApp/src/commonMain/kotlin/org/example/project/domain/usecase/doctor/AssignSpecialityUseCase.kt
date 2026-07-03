package org.example.project.domain.usecase.doctor

import org.example.project.domain.repository.doctor.DoctorRepository

class AssignSpecialityUseCase(
    private val repository: DoctorRepository
) {
    suspend operator fun invoke(
        doctorId: String,
        specialityId: Int,
        isPrimary: Boolean,
        yearsOfExperience: Int
    ): Result<Unit> {
        return repository.assignSpeciality(doctorId, specialityId, isPrimary, yearsOfExperience)
    }
}
