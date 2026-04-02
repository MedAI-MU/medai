package org.example.project.domain.usecase

import org.example.project.domain.model.*
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
    suspend operator fun invoke(patient: CreatePatientParams): Result<Boolean> {
        return repository.createPatient(patient)
    }
}

class UpdatePatientUseCase(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(id: String, patient: UpdatePatientParams): Result<Boolean> {
        return repository.updatePatient(id, patient)
    }
}
