package org.example.project.design_system.component.dayPicker

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant
import org.example.project.design_system.theme.MedAITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedAIDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    allowFutureDates: Boolean = false,
    allowPastDates: Boolean = true,
    outputFormat: String = "DD / MM / YYYY"
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
                if (!allowPastDates) return year >= currentYear
                if (!allowFutureDates) return year <= currentYear
                return true
            }

            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val todayUtcStart = kotlinx.datetime.LocalDateTime(today.year, today.monthNumber, today.dayOfMonth, 0, 0, 0)
                    .toInstant(TimeZone.UTC)
                    .toEpochMilliseconds()

                if (!allowPastDates) {
                    // Only allow today and future dates. We use a buffer of 12 hours/timezone tolerance if needed,
                    // but using start of day today in UTC is standard for Calendar components which return UTC millis.
                    return utcTimeMillis >= todayUtcStart
                }
                if (!allowFutureDates) {
                    val now = Clock.System.now().toEpochMilliseconds()
                    return utcTimeMillis <= now
                }
                return true
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis
                if (selectedDateMillis != null) {
                    val formattedDate = if (outputFormat == "YYYY-MM-DD") {
                        convertMillisToYyyyMmDd(selectedDateMillis)
                    } else {
                        convertMillisToDate(selectedDateMillis)
                    }
                    onDateSelected(formattedDate)
                }
                onDismiss()
            }) {
                Text("OK", color = MedAITheme.colors.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MedAITheme.colors.text.secondary)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MedAITheme.colors.surface,
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                // --- Container ---
                containerColor = MedAITheme.colors.surface,

                // --- Header (The selected date at top) ---
                headlineContentColor = MedAITheme.colors.text.primary,
                titleContentColor = MedAITheme.colors.text.secondary,

                // --- Navigation (< > arrows) ---
                navigationContentColor = MedAITheme.colors.text.primary,

                // --- Year Selection ---
                currentYearContentColor = MedAITheme.colors.primary,
                selectedYearContentColor = MedAITheme.colors.onPrimary,
                selectedYearContainerColor = MedAITheme.colors.primary,
                yearContentColor = MedAITheme.colors.text.primary,

                // --- Calendar Grid ---
                weekdayContentColor = MedAITheme.colors.text.secondary,

                // Normal Days
                dayContentColor = MedAITheme.colors.text.primary,
                disabledDayContentColor = MedAITheme.colors.text.tertiary,
                selectedDayContentColor = MedAITheme.colors.text.onPrimary,

                // Today
                todayContentColor = MedAITheme.colors.primary,
                todayDateBorderColor = MedAITheme.colors.primary,

                // Selection Circle
                selectedDayContainerColor = MedAITheme.colors.primary
            )
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val date = instant.toLocalDateTime(TimeZone.UTC).date
    return "${date.dayOfMonth.toString().padStart(2, '0')} / ${date.monthNumber.toString().padStart(2, '0')} / ${date.year}"
}

fun convertMillisToYyyyMmDd(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val date = instant.toLocalDateTime(TimeZone.UTC).date
    return "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
}
