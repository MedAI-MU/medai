package org.example.project.domain.repository.scans

import org.example.project.domain.model.scans.Scan
import org.example.project.domain.model.scans.Report

interface ScansRepository {
    suspend fun getScans(patientUserId: String? = null): Result<List<Scan>>

    suspend fun getScanDetails(patientUserId: String, scanId: String): Result<Scan>

    suspend fun uploadScan(
        patientUserId: String,
        appointmentId: String?,
        images: List<ByteArray>
    ): Result<Scan>

    suspend fun deleteScan(patientUserId: String, scanId: String): Result<Unit>

    suspend fun getScanReports(patientUserId: String, scanId: String): Result<List<Report>>

    suspend fun uploadReport(
        patientUserId: String,
        scanId: String,
        fileBytes: ByteArray,
        fileName: String
    ): Result<Report>

    suspend fun deleteReport(patientUserId: String, reportId: String): Result<Unit>

    suspend fun getScanImageFile(patientUserId: String, imageId: String): Result<ByteArray>

    suspend fun getReportFile(patientUserId: String, reportId: String): Result<ByteArray>
}
