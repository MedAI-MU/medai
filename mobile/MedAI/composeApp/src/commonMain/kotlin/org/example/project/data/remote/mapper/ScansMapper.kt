package org.example.project.data.remote.mapper

import org.example.project.data.remote.dto.scans.ScanResponseDto
import org.example.project.data.remote.dto.scans.ScanImageDto
import org.example.project.data.remote.dto.scans.ReportResponseDto
import org.example.project.domain.model.scans.Scan
import org.example.project.domain.model.scans.ScanImage
import org.example.project.domain.model.scans.Report

fun ScanResponseDto.toDomain(): Scan {
    return Scan(
        id = this.id.toString(),
        patientUserId = this.patientUserId.toString(),
        appointmentId = this.appointmentId?.toString(),
        images = this.images.map { it.toDomain() },
        createdAt = this.createdAt
    )
}

fun ScanImageDto.toDomain(): ScanImage {
    return ScanImage(
        id = this.id.toString(),
        path = this.path
    )
}

fun ReportResponseDto.toDomain(): Report {
    return Report(
        id = this.id.toString(),
        patientUserId = this.patientUserId.toString(),
        scanId = this.scanId?.toString(),
        path = this.path,
        createdAt = this.createdAt
    )
}
