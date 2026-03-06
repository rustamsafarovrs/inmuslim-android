package tj.rsdevteam.inmuslim.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun testFindCurrentPrayer() {
        val timing = Timing(
            fajr = "04:00",
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
        assertEquals("21:00", TimeUtils.findCurrentPrayer(timing, 3 * 60, 1, 2, 3, 4, 5)?.startTime)

        // At Fajr
        assertEquals(1, TimeUtils.findCurrentPrayer(timing, 4 * 60, 1, 2, 3, 4, 5)?.nameResId)
        assertEquals("04:00", TimeUtils.findCurrentPrayer(timing, 4 * 60, 1, 2, 3, 4, 5)?.startTime)
        assertEquals("05:59", TimeUtils.findCurrentPrayer(timing, 4 * 60, 1, 2, 3, 4, 5)?.endTime)

        // After Sunrise, before Zuhr
        assertNull(TimeUtils.findCurrentPrayer(timing, 7 * 60, 1, 2, 3, 4, 5))

        // At Zuhr
        val zuhrInfo = TimeUtils.findCurrentPrayer(timing, 13 * 60, 1, 2, 3, 4, 5)
        assertEquals(2, zuhrInfo?.nameResId)
        assertEquals("13:00", zuhrInfo?.startTime)
        assertEquals("15:59", zuhrInfo?.endTime)

        // At Maghrib
        val maghribInfo = TimeUtils.findCurrentPrayer(timing, 19 * 60 + 5, 1, 2, 3, 4, 5)
        assertEquals(4, maghribInfo?.nameResId)
        assertEquals("19:44", maghribInfo?.endTime)

        // At Isha
        val ishaInfo = TimeUtils.findCurrentPrayer(timing, 21 * 60, 1, 2, 3, 4, 5)
        assertEquals(5, ishaInfo?.nameResId)
        assertEquals("21:00", ishaInfo?.startTime)
        assertEquals("03:59", ishaInfo?.endTime)
    }
}
