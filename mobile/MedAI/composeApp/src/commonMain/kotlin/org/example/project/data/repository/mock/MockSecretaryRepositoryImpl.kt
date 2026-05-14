package org.example.project.data.repository.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.example.project.domain.model.patient.BloodType
import org.example.project.domain.model.patient.Gender
import org.example.project.domain.model.patient.MaritalStatus
import org.example.project.domain.model.patient.Patient
import org.example.project.domain.model.secretary.ClinicStats
import org.example.project.domain.model.secretary.Invoice
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.model.secretary.QueueStatus
import org.example.project.domain.repository.secretary.SecretaryRepository

class MockSecretaryRepositoryImpl : SecretaryRepository {

    // --- Mock Data ---

    private val patients = MutableStateFlow(
        listOf(
            Patient("1", "John Doe", Gender.Male, 1, "1234567890", 17.85, 22.5, BloodType.B_NEG, MaritalStatus.Married),
            Patient("2", "Jane Smith", Gender.Female, 2, "0987654321", 22.4, 33.6, BloodType.B_NEG, MaritalStatus.Married),
            Patient("3", "Alice Johnson", Gender.Female, 5, "1122334455", 22.5, 44.2, BloodType.B_NEG, MaritalStatus.Married),
            Patient("4", "Bob Brown", Gender.Male, 6, "5566778899", 33.5, 33.3, BloodType.B_NEG, MaritalStatus.Married),
            Patient("5", "Charlie Davis", Gender.Male, 8, "6677889900", 41.5, 33.5, BloodType.B_NEG, MaritalStatus.Married),
        )
    )

    private val queues = MutableStateFlow(
        listOf(
            QueueEntry("1", "1", "John Doe", "doc1", "Dr. Smith", "2023-10-27T09:00:00", QueueStatus.WAITING),
            QueueEntry("2", "2", "Jane Smith", "doc1", "Dr. Smith", "2023-10-27T09:30:00", QueueStatus.IN_PROGRESS),
            QueueEntry("3", "3", "Alice Johnson", "doc2", "Dr. Jones", "2023-10-27T10:00:00", QueueStatus.COMPLETED),
            QueueEntry("4", "4", "Bob Brown", "doc2", "Dr. Jones", "2023-10-27T10:30:00", QueueStatus.WAITING),
             QueueEntry("5", "5", "Charlie Davis", "doc3", "Dr. Emily", "2023-10-27T11:00:00", QueueStatus.WAITING)
        )
    )

    private val invoices = MutableStateFlow(
        listOf(
            Invoice("1", "3", "Alice Johnson", 50.0, "PAID", "2023-10-27", listOf("Consultation", "Blood Test")),
            Invoice("2", "2", "Jane Smith", 75.0, "PENDING", "2023-10-27", listOf("Consultation", "X-Ray"))
        )
    )

    // --- Implementation ---

    override fun getClinicStats(): Flow<ClinicStats> {
        return queues.map { queue ->
            ClinicStats(
                totalPatientsToday = queue.size,
                activeDoctors = queue.map { it.doctorId }.distinct().size,
                totalRevenueToday = invoices.value.sumOf { if(it.status == "PAID") it.amount else 0.0 }, // Simple calc
                pendingAppointments = queue.count { it.status == QueueStatus.WAITING }
            )
        }
    }

    override suspend fun getAllPatients(): List<Patient> {
        return patients.value
    }

    override suspend fun getPatientById(patientId: String): Patient? {
        return patients.value.find { it.id == patientId }
    }

    override suspend fun createPatient(patient: Patient): Result<Patient> {
        val newPatient = patient.copy(id = (patients.value.size + 1).toString())
        patients.value += newPatient
        return Result.success(newPatient)
    }

    override suspend fun searchPatients(query: String): List<Patient> {
        return patients.value.filter {
            it.fullName.contains(query, ignoreCase = true)
        }
    }

    override fun getQueueForDoctor(doctorId: String): Flow<List<QueueEntry>> {
        return queues.map { list -> list.filter { it.doctorId == doctorId } }
    }

    override fun getAllQueues(): Flow<List<QueueEntry>> {
        return queues
    }

    override suspend fun updateQueueStatus(entryId: String, status: QueueStatus) {
        val currentList = queues.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == entryId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(status = status)
            queues.value = currentList
        }
    }

    override suspend fun checkInPatient(appointmentId: String) {
        // In this mock, appointmentId maps 1:1 to queueId for simplicity, or we create a new queue entry
        // For now, let's assume checking in means creating a queue entry or updating status to WAITING
        // Let's assume we update status to ARRIVED/WAITING
        updateQueueStatus(appointmentId, QueueStatus.WAITING)
    }

    override suspend fun generateInvoice(patientId: String, items: List<String>, amount: Double): Result<Invoice> {
        val patient = getPatientById(patientId) ?: return Result.failure(Exception("Patient not found"))
        val invoice = Invoice(
            id = (invoices.value.size + 1).toString(),
            patientId = patientId,
            patientName = patient.fullName,
            amount = amount,
            status = "PENDING",
            date = "2023-10-27", // Mock date
            items = items
        )
        invoices.value += invoice
        return Result.success(invoice)
    }

    override suspend fun getPendingInvoices(): List<Invoice> {
        return invoices.value.filter { it.status == "PENDING" }
    }

    override suspend fun markInvoiceAsPaid(invoiceId: String) {
        val currentList = invoices.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == invoiceId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(status = "PAID")
            invoices.value = currentList
            // Trigger flow update if we had a flow for invoices, but here we just update state
        }
    }
}
