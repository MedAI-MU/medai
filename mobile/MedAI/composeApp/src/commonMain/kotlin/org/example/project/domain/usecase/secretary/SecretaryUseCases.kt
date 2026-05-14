package org.example.project.domain.usecase.secretary

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.patient.Patient
import org.example.project.domain.model.secretary.ClinicStats
import org.example.project.domain.model.secretary.Invoice
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.repository.secretary.SecretaryRepository

class GetDashboardStatsUseCase(
    private val repository: SecretaryRepository
) {
    operator fun invoke(): Flow<ClinicStats> {
        return repository.getClinicStats()
    }
}

class GetAllPatientsUseCase(
    private val repository: SecretaryRepository
) {
    suspend operator fun invoke(): List<Patient> {
        return repository.getAllPatients()
    }
}

class CreatePatientUseCase(
    private val repository: SecretaryRepository
) {
    suspend operator fun invoke(patient: Patient): Result<Patient> {
        if (patient.fullName.isBlank()) return Result.failure(Exception("Name cannot be empty"))
        return repository.createPatient(patient)
    }
}

class CheckInPatientUseCase(
    private val repository: SecretaryRepository
) {
    suspend operator fun invoke(appointmentId: String) {
        repository.checkInPatient(appointmentId)
    }
}

class GetDoctorQueueUseCase(
    private val repository: SecretaryRepository
) {
    operator fun invoke(doctorId: String): Flow<List<QueueEntry>> {
        return repository.getQueueForDoctor(doctorId)
    }
}

class GetAllQueuesUseCase(
    private val repository: SecretaryRepository
) {
    operator fun invoke(): Flow<List<QueueEntry>> {
        return repository.getAllQueues()
    }
}

class GenerateInvoiceUseCase(
    private val repository: SecretaryRepository
) {
    suspend operator fun invoke(patientId: String, items: List<String>, amount: Double): Result<Invoice> {
        return repository.generateInvoice(patientId, items, amount)
    }
}
