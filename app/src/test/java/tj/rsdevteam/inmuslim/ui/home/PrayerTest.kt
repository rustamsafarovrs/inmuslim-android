package tj.rsdevteam.inmuslim.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test
import tj.rsdevteam.inmuslim.data.models.Timing

class PrayerTest {

    @Test
    fun `reads each prayer's own time`() {
        val timing = Timing(
            fajr = "fajr",
            sunrise = "sunrise",
            zuhr = "zuhr",
            asr = "asr",
            sunset = "sunset",
            maghrib = "maghrib",
            isha = "isha",
        )

        Prayer.entries.forEach { prayer ->
            assertEquals(prayer.name.lowercase(), timing.timeOf(prayer))
        }
    }
}
