package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentDto(
    val id: String,
    val doctor: DoctorDto,
    val date: String,
    val time: String,
    val status: String
)
