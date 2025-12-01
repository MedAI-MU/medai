package org.example.project.core.presentation.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class CalendarManager {

    fun getToday(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    fun getDaysForMonth(baseDate: LocalDate): List<LocalDate> {
        val firstDayOfMonth = LocalDate(baseDate.year, baseDate.monthNumber, 1)
        val daysInMonth = mutableListOf<LocalDate>()
        var current = firstDayOfMonth

        // Loop until month changes
        while (current.month == firstDayOfMonth.month) {
            daysInMonth.add(current)
            current = current.plus(DatePeriod(days = 1))
        }
        return daysInMonth
    }

    fun getPreviousMonth(current: LocalDate): LocalDate {
        val firstDay = LocalDate(current.year, current.monthNumber, 1)
        return firstDay.minus(DatePeriod(months = 1))
    }

    fun getNextMonth(current: LocalDate): LocalDate {
        val firstDay = LocalDate(current.year, current.monthNumber, 1)
        return firstDay.plus(DatePeriod(months = 1))
    }
}
