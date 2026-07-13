package org.example.project.domain.usecase.voice_report

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.model.voice_report.VoiceReport
import org.example.project.domain.repository.appointment.AppointmentRepository
import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.repository.voice_report.VoiceReportRepository

class GetPatientComplaintsHistoryUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val voiceReportRepository: VoiceReportRepository,
    private val sessionManager: UserSessionManager
) {
    suspend operator fun invoke(patientId: String): Result<List<VoiceReport>> {
        return try {
            val role = sessionManager.getUserRole()
            val appointmentsResult = if (role == UserRole.SECRETARY || role == UserRole.MANAGER) {
                appointmentRepository.getAllAppointments()
            } else {
                appointmentRepository.getMyAppointments()
            }
            val appointments = appointmentsResult.getOrNull() ?: emptyList()

            // Filter appointments associated with the specific patient ID
            val patientAppointmentIds = appointments
                .filter { it.patientId == patientId }
                .map { it.id }

            if (patientAppointmentIds.isEmpty()) {
                return Result.success(emptyList())
            }

            // Concurrently fetch voice reports for all these appointments
            val reports = coroutineScope {
                patientAppointmentIds.map { apptId ->
                    async {
                        voiceReportRepository.getReports(apptId).getOrNull() ?: emptyList()
                    }
                }.awaitAll().flatten()
            }

            // Sort chronologically (newest first)
            val sortedReports = reports.sortedByDescending { it.createdAt }
            Result.success(sortedReports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
