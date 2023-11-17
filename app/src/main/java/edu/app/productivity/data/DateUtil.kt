package edu.app.productivity.data

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Date

fun calendarDaysBetween(first: Date, second: Date): Int = calendarDaysBetween(
    first.toInstant().atZone(ZoneId.systemDefault()),
    second.toInstant().atZone(ZoneId.systemDefault())
)

fun calendarDaysBetween(first: ZonedDateTime, second: ZonedDateTime): Int =
    if (first.isAfter(second))
        calendarDaysBetween(second, first)
    else {
        val firstDay = first.truncatedTo(ChronoUnit.DAYS)
        val secondDay = second.truncatedTo(ChronoUnit.DAYS)

        secondDay.minusDays(firstDay.getLong(ChronoField.EPOCH_DAY))
            .getLong(ChronoField.EPOCH_DAY).toInt()
    }
