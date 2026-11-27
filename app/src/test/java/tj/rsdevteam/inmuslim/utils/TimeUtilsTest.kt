package tj.rsdevteam.inmuslim.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class TimeUtilsTest {

    @Test
    fun testTimeToMinutes() {
        assertEquals(0, TimeUtils.timeToMinutes("00:00"))
        assertEquals(60, TimeUtils.timeToMinutes("01:00"))
        assertEquals(13 * 60 + 30, TimeUtils.timeToMinutes("13:30"))
        assertEquals(13 * 60 + 30, TimeUtils.timeToMinutes("13:30:45"))
    }

    @Test
    fun testFormatTime() {
        // 24-hour format (default)
        assertEquals("00:00", TimeUtils.formatTime("00:00"))
        assertEquals("01:00", TimeUtils.formatTime("1:00"))
        assertEquals("13:30", TimeUtils.formatTime("13:30"))
        assertEquals("13:30", TimeUtils.formatTime("13:30:45"))
        assertEquals("05:05", TimeUtils.formatTime("5:5:10"))
        assertEquals("invalid", TimeUtils.formatTime("invalid"))

        // 12-hour format
        assertEquals("12:00 AM", TimeUtils.formatTime("00:00", false))
        assertEquals("1:00 AM", TimeUtils.formatTime("1:00", false))
        assertEquals("12:00 PM", TimeUtils.formatTime("12:00", false))
        assertEquals("1:30 PM", TimeUtils.formatTime("13:30", false))
        assertEquals("11:59 PM", TimeUtils.formatTime("23:59", false))
    }

    @Test
    fun testFormatPrayerBoundaries() {
        // Formatting for display (24-hour)
        assertEquals("04:00", TimeUtils.formatTime("04:00:10"))
        assertEquals("05:59", TimeUtils.formatMinutes(6 * 60 - 1))

        // Formatting for display (12-hour)
        assertEquals("9:00 PM", TimeUtils.formatTime("21:00", false))
        assertEquals("3:59 AM", TimeUtils.formatMinutes(3 * 60 + 59, false))
        assertEquals("1:00 PM", TimeUtils.formatTime("13:00", false))
        assertEquals("3:59 PM", TimeUtils.formatMinutes(16 * 60 - 1, false))
    }

    @Test
    fun testFormatCountdown() {
        assertEquals("0:00", TimeUtils.formatCountdown(0))
        assertEquals("0:59", TimeUtils.formatCountdown(59))
        assertEquals("1:00", TimeUtils.formatCountdown(60))
        assertEquals("45:12", TimeUtils.formatCountdown(45 * 60 + 12))
        assertEquals("59:59", TimeUtils.formatCountdown(3599))
        assertEquals("1:00:00", TimeUtils.formatCountdown(3600))
        assertEquals("1:29:30", TimeUtils.formatCountdown(89 * 60 + 30))
        assertEquals("12:00:00", TimeUtils.formatCountdown(12 * 3600))
        assertEquals("0:00", TimeUtils.formatCountdown(-5))
    }

    @Test
    fun `reads the time of day and the date from a timestamp`() {
        val millis = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JANUARY, 5, 13, 30, 45)
        }.timeInMillis

        assertEquals(13 * 3600 + 30 * 60 + 45, TimeUtils.secondsOfDay(millis))
        assertEquals("2026-01-05", TimeUtils.isoDate(millis))
    }
}
