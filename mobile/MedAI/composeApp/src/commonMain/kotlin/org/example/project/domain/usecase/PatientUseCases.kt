package org.example.project.domain.usecase

import org.example.project.data.remote.dto.CreatePatientDto
import org.example.project.data.remote.dto.UpdatePatientDto
import org.example.project.domain.model.Patient
import org.example.project.domain.repository.PatientRepository

class GetPatientsUseCase(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(): Result<List<Patient>> {
        return repository.getPatients()
    }
}

class GetPatientByIdUseCase(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(id: String): Result<Patient> {
        return repository.getPatientById(id)
    }
}

class CreatePatientUseCase(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(patient: CreatePatientDto): Result<Boolean> {
        return repository.createPatient(patient)
    }
}

class UpdatePatientUseCase(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(id: String, patient: UpdatePatientDto): Result<Boolean> {
        return repository.updatePatient(id, patient)
    }
}
