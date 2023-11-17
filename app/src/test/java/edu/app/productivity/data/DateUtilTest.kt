package edu.app.productivity.data

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class DateUtilTest {

    private fun zonedDateTime(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hour: Int,
        minute: Int,
        second: Int,
        nanoOfSecond: Int,
        zone: ZoneId = ZoneId.systemDefault()
    ) = ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zone)

    @Test
    fun testCalendarDaysBetweenDates() {
        val date1 = zonedDateTime(
            year = 2020,
            month = 6,
            dayOfMonth = 15,
            hour = 12,
            minute = 0,
            second = 0,
            nanoOfSecond = 0
        )

        val date2 = zonedDateTime(
            year = 2020,
            month = 6,
            dayOfMonth = 16,
            hour = 12,
            minute = 0,
            second = 0,
            nanoOfSecond = 0
        )

        assertEquals(1, calendarDaysBetween(date1, date2))
        assertEquals(1, calendarDaysBetween(date2, date1))

        val date3 = zonedDateTime(
            year = 2020,
            month = 6,
            dayOfMonth = 16,
            hour = 23,
            minute = 59,
            second = 59,
            nanoOfSecond = 0
        )

        val date4 = date3.plusSeconds(1L)

        assertEquals(1, calendarDaysBetween(date3, date4))
        assertEquals(1, calendarDaysBetween(date4, date3))

        val date5 = date3.truncatedTo(ChronoUnit.DAYS)
        assertEquals(0, calendarDaysBetween(date3, date5))
        assertEquals(0, calendarDaysBetween(date5, date3))

        assertEquals(0, calendarDaysBetween(date1, date1))
        assertEquals(0, calendarDaysBetween(date3, date3))
    }
}