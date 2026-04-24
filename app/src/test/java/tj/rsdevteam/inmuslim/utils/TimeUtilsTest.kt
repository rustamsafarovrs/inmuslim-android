package tj.rsdevteam.inmuslim.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import tj.rsdevteam.inmuslim.data.models.Timing

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
    fun testFindCurrentPrayer() {
        val timing = Timing(
            fajr = "04:00:10",
            sunrise = "06:00",
            zuhr = "13:00",
            asr = "16:00",
            sunset = "19:00",
            maghrib = "19:05",
            isha = "21:00"
        )

        // fajrResId=1, zuhrResId=2, asrResId=3, maghribResId=4, ishaResId=5 for testing

        // Before Fajr (Isha from previous day)
        assertEquals(5, TimeUtils.findCurrentPrayer(timing, 3 * 60, 1, 2, 3, 4, 5)?.nameResId)
        assertEquals("21:00", TimeUtils.findCurrentPrayer(timing, 3 * 60, 1, 2, 3, 4, 5)?.startTimeRaw)

        // At Fajr
        assertEquals(1, TimeUtils.findCurrentPrayer(timing, 4 * 60, 1, 2, 3, 4, 5)?.nameResId)
        assertEquals("04:00:10", TimeUtils.findCurrentPrayer(timing, 4 * 60, 1, 2, 3, 4, 5)?.startTimeRaw)
        assertEquals(6 * 60 - 1, TimeUtils.findCurrentPrayer(timing, 4 * 60, 1, 2, 3, 4, 5)?.endInMinutes)

        // Formatting for display (24-hour)
        assertEquals("04:00", TimeUtils.formatTime("04:00:10"))
        assertEquals("05:59", TimeUtils.formatMinutes(6 * 60 - 1))

        // Formatting for display (12-hour)
        assertEquals("9:00 PM", TimeUtils.formatTime("21:00", false))
        assertEquals("3:59 AM", TimeUtils.formatMinutes(3 * 60 + 59, false))
        assertEquals("1:00 PM", TimeUtils.formatTime("13:00", false))
        assertEquals("3:59 PM", TimeUtils.formatMinutes(16 * 60 - 1, false))
    }
}
