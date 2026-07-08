package org.example.project.domain.model.scans

data class Scan(
    val id: String,
    val patientUserId: String,
    val appointmentId: String?,
    val images: List<ScanImage>,
    val createdAt: String
)

data class ScanImage(
    val id: String,
    val path: String
)
