package com.hogargo.app.data

import java.time.LocalDate

/**
 * A streak is the number of consecutive days, ending today or yesterday, on which the household
 * completed at least one task. Dates are ISO strings (yyyy-MM-dd), as stored on the tasks.
 */
object StreakCalculator {

    fun today(): String = LocalDate.now().toString()

    fun streakDays(completedDates: Collection<String>, today: LocalDate = LocalDate.now()): Int {
        val days = completedDates.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.toSet()
        // Still alive if the last completion was today or yesterday (today isn't over yet).
        var day = when {
            today in days -> today
            today.minusDays(1) in days -> today.minusDays(1)
            else -> return 0
        }
        var count = 0
        while (day in days) {
            count++
            day = day.minusDays(1)
        }
        return count
    }

    /** True when there is a streak to lose and nothing has been completed today yet. */
    fun isAtRisk(completedDates: Collection<String>, today: LocalDate = LocalDate.now()): Boolean =
        streakDays(completedDates, today) > 0 && today.toString() !in completedDates
}
