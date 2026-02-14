package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Patient
import org.example.project.domain.model.secretary.ClinicStats
import org.example.project.domain.model.secretary.Invoice
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.model.secretary.QueueStatus

interface SecretaryRepository {
    // Stats
    fun getClinicStats(): Flow<ClinicStats>

    // Patient Management
    suspend fun getAllPatients(): List<Patient>
    suspend fun getPatientById(patientId: String): Patient?
    suspend fun createPatient(patient: Patient): Result<Patient>
    suspend fun searchPatients(query: String): List<Patient>

    // Queue Management
    fun getQueueForDoctor(doctorId: String): Flow<List<QueueEntry>>
    fun getAllQueues(): Flow<List<QueueEntry>>
    suspend fun updateQueueStatus(entryId: String, status: QueueStatus)
    suspend fun checkInPatient(appointmentId: String)

    // Billing
    suspend fun generateInvoice(patientId: String, items: List<String>, amount: Double): Result<Invoice>
    suspend fun getPendingInvoices(): List<Invoice>
    suspend fun markInvoiceAsPaid(invoiceId: String)
}
