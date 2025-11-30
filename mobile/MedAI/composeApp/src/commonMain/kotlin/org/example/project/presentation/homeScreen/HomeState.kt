package org.example.project.presentation.homeScreen

import kotlinx.datetime.LocalDate
import org.example.project.domain.model.Appointment
import org.example.project.domain.model.Category
import org.example.project.domain.model.Specialty

data class HomeState(
    val userName: String = "",
    val categories: List<Category> = emptyList(),
    val allAppointments: List<Appointment> = emptyList(),
    val filteredAppointments: List<Appointment> = emptyList(),
    val specialties: List<Specialty> = emptyList(),
    val calendarDays: List<CalendarUiModel> = emptyList(),
    val selectedDate: LocalDate? = null,
    val displayedMonth: LocalDate? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class CalendarUiModel(
    val day: String,      // e.g. "9"
    val weekDay: String,  // e.g. "MON"
    val fullDate: LocalDate, // e.g. "2025-11-09"
    val isSelected: Boolean = false
)
