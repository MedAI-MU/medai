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
import org.example.project.design_system.theme.MedAITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedAIDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
                return year <= currentYear
            }
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis
                if (selectedDateMillis != null) {
                    // Convert Millis to "DD / MM / YYYY"
                    val formattedDate = convertMillisToDate(selectedDateMillis)
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
