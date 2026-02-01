package org.example.project.core.presentation.util

import kotlinx.datetime.LocalDateTime

fun LocalDateTime.toUiString(): String {
    val monthName = this.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    val day = this.dayOfMonth.toString().padStart(2, '0')
    val dayOfWeek = this.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)

    val hour = if (this.hour == 0 || this.hour == 12) 12 else this.hour % 12
    val amPm = if (this.hour < 12) "AM" else "PM"
    val minute = this.minute.toString().padStart(2, '0')

    return "$dayOfWeek, $day $monthName | $hour:$minute $amPm"
}
