package org.example.project.domain.repository

import org.example.project.data.remote.dto.CreatePatientDto
import org.example.project.data.remote.dto.UpdatePatientDto
import org.example.project.domain.model.Patient

interface PatientRepository {
    suspend fun getPatients(): Result<List<Patient>>
    suspend fun getPatientById(id: String): Result<Patient>
    suspend fun createPatient(patient: CreatePatientDto): Result<Boolean>
    suspend fun updatePatient(id: String, patient: UpdatePatientDto): Result<Boolean>
}
