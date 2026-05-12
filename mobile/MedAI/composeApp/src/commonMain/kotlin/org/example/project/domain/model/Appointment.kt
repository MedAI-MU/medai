package org.example.project.domain.model

import kotlinx.datetime.LocalDate

data class Appointment(
    val id: String,
    val doctor: Doctor,
    val date: LocalDate,
    val time: String,
    val status: AppointmentStatus,
    val type: AppointmentType = AppointmentType.General
)

enum class AppointmentStatus {
    Pending, Confirmed, Finished, Cancelled
}

enum class AppointmentType {
    General, Dentists, Ophthalmic, Nutritionist, Neurologist, Pediatric
}
