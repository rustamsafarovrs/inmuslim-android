package tj.rsdevteam.inmuslim.ui.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import tj.rsdevteam.inmuslim.data.models.Timing

class PrayerScheduleTest {

    private val timing = Timing(
        fajr = "04:00:10",
        sunrise = "06:00",
        zuhr = "13:00",
        asr = "16:00",
        sunset = "19:00",
        maghrib = "19:05",
        isha = "21:00",
    )

    private fun secondsOf(hours: Int, minutes: Int = 0, seconds: Int = 0) = (hours * 60 + minutes) * 60 + seconds

    private fun at(hours: Int, minutes: Int = 0, seconds: Int = 0) =
        PrayerSchedule.heroAt(timing, secondsOf(hours, minutes, seconds))

    @Test
    fun `fajr is active from its start until sunrise`() {
        val hero = at(4)

        assertEquals(Prayer.FAJR, hero.prayer)
        assertTrue(hero.isActive)
        assertEquals(4 * 60, hero.startInMinutes)
        assertEquals(6 * 60, hero.endInMinutes)
        assertEquals(120 * 60, hero.secondsLeft)
        assertEquals(0f, hero.progress)
    }

    @Test
    fun `asr reports how far through its window it is`() {
        val hero = at(17, 30)

        assertEquals(Prayer.ASR, hero.prayer)
        assertEquals(90 * 60, hero.secondsLeft)
        assertEquals(0.5f, hero.progress)
    }

    @Test
    fun `counts down and advances progress every second`() {
        val hero = at(17, 30, 30)

        assertEquals(Prayer.ASR, hero.prayer)
        assertEquals(89 * 60 + 30, hero.secondsLeft)
        assertEquals((90 * 60 + 30) / (180f * 60), hero.progress)
    }

    @Test
    fun `last second of a window still belongs to it`() {
        val hero = at(15, 59, 59)

        assertEquals(Prayer.ZUHR, hero.prayer)
        assertTrue(hero.isActive)
        assertEquals(1, hero.secondsLeft)
    }

    @Test
    fun `isha before fajr started the previous evening`() {
        val hero = at(3)

        assertEquals(Prayer.ISHA, hero.prayer)
        assertTrue(hero.isActive)
        assertEquals(21 * 60, hero.startInMinutes)
        assertEquals(4 * 60, hero.endInMinutes)
        assertEquals(60 * 60, hero.secondsLeft)
    }

    @Test
    fun `isha after its start runs until next fajr`() {
        val hero = at(22)

        assertEquals(Prayer.ISHA, hero.prayer)
        assertEquals(4 * 60, hero.endInMinutes)
        assertEquals(6 * 60 * 60, hero.secondsLeft)
    }

    @Test
    fun `maghrib lasts forty minutes`() {
        assertEquals(Prayer.MAGHRIB, at(19, 44).prayer)
        assertTrue(at(19, 44).isActive)
        assertFalse(at(19, 45).isActive)
    }

    @Test
    fun `between sunrise and zuhr waits for zuhr`() {
        val hero = at(9)

        assertEquals(Prayer.ZUHR, hero.prayer)
        assertFalse(hero.isActive)
        assertEquals(6 * 60, hero.startInMinutes)
        assertEquals(13 * 60, hero.endInMinutes)
        assertEquals(240 * 60, hero.secondsLeft)
    }

    @Test
    fun `between sunset and maghrib waits for maghrib`() {
        val hero = at(19, 4)

        assertEquals(Prayer.MAGHRIB, hero.prayer)
        assertFalse(hero.isActive)
        assertEquals(1 * 60, hero.secondsLeft)
    }

    @Test
    fun `after maghrib ends waits for isha`() {
        val hero = at(20)

        assertEquals(Prayer.ISHA, hero.prayer)
        assertFalse(hero.isActive)
        assertEquals(19 * 60 + 45, hero.startInMinutes)
        assertEquals(60 * 60, hero.secondsLeft)
    }

    @Test
    fun `marks past, current and future prayers`() {
        val now = secondsOf(17)
        val statuses = PrayerSchedule.statusesAt(timing, PrayerSchedule.heroAt(timing, now), now)

        assertEquals(PrayerStatus.PAST, statuses[Prayer.ZUHR])
        assertEquals(PrayerStatus.NOW, statuses[Prayer.ASR])
        assertEquals(PrayerStatus.FUTURE, statuses[Prayer.MAGHRIB])
    }

    @Test
    fun `marks the upcoming prayer as next between prayers`() {
        val now = secondsOf(9)
        val statuses = PrayerSchedule.statusesAt(timing, PrayerSchedule.heroAt(timing, now), now)

        assertEquals(PrayerStatus.PAST, statuses[Prayer.FAJR])
        assertEquals(PrayerStatus.NEXT, statuses[Prayer.ZUHR])
        assertEquals(PrayerStatus.FUTURE, statuses[Prayer.ASR])
    }

    @Test
    fun `everything else is past while isha is active after midnight`() {
        val now = secondsOf(3)
        val statuses = PrayerSchedule.statusesAt(timing, PrayerSchedule.heroAt(timing, now), now)

        assertEquals(PrayerStatus.NOW, statuses[Prayer.ISHA])
        assertEquals(PrayerStatus.PAST, statuses[Prayer.FAJR])
        assertEquals(PrayerStatus.PAST, statuses[Prayer.MAGHRIB])
    }
}
