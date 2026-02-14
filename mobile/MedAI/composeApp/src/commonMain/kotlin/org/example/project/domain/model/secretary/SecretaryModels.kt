package org.example.project.domain.model.secretary

import kotlinx.serialization.Serializable
import org.example.project.domain.model.Doctor
import org.example.project.domain.model.Patient

enum class QueueStatus {
    WAITING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

@Serializable
data class QueueEntry(
    val id: String,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val appointmentTime: String, // ISO String
    val status: QueueStatus,
    val checkInTime: String? = null
)

@Serializable
data class Invoice(
    val id: String,
    val patientId: String,
    val patientName: String,
    val amount: Double,
    val status: String, // "PAID", "PENDING", "CANCELLED"
    val date: String,
    val items: List<String>
)

@Serializable
data class ClinicStats(
    val totalPatientsToday: Int,
    val activeDoctors: Int,
    val totalRevenueToday: Double,
    val pendingAppointments: Int
)
