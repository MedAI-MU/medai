package org.example.project.domain.usecase.patient

import org.example.project.domain.model.auth.*
import org.example.project.domain.model.appointment.*
import org.example.project.domain.model.chat.*
import org.example.project.domain.model.doctor.*
import org.example.project.domain.model.home.*
import org.example.project.domain.model.medical_record.*
import org.example.project.domain.model.notification.*
import org.example.project.domain.model.patient.*
import org.example.project.domain.model.specialty.*
import org.example.project.domain.repository.patient.PatientRepository

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
