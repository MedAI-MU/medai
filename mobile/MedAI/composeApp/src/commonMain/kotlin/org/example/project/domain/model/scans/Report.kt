package org.example.project.domain.model.scans

data class Report(
    val id: String,
    val patientUserId: String,
    val scanId: String?,
    val path: String,
    val createdAt: String
)
