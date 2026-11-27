package tj.rsdevteam.inmuslim.ui.home

import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.utils.TimeUtils

object PrayerSchedule {

    private const val SECONDS_IN_MINUTE = 60
    private const val MINUTES_IN_DAY = 24 * 60
    private const val MAGHRIB_MINUTES = 40

    private data class Window(val prayer: Prayer, val start: Int, val end: Int)

    fun heroAt(timing: Timing, nowInSeconds: Int): PrayerHero {
        val now = nowInSeconds / SECONDS_IN_MINUTE
        val current = currentWindow(timing, now)
        return current?.toHero(nowInSeconds, isActive = true)
            ?: nextWindow(timing, now).toHero(nowInSeconds, isActive = false)
    }

    fun statusesAt(timing: Timing, hero: PrayerHero, nowInSeconds: Int): Map<Prayer, PrayerStatus> {
        val now = nowInSeconds / SECONDS_IN_MINUTE
        return Prayer.entries.associateWith { prayer ->
            when {
                prayer == hero.prayer -> if (hero.isActive) PrayerStatus.NOW else PrayerStatus.NEXT
                hero.isActive && hero.prayer == Prayer.ISHA -> PrayerStatus.PAST
                TimeUtils.timeToMinutes(timing.timeOf(prayer)) <= now -> PrayerStatus.PAST
                else -> PrayerStatus.FUTURE
            }
        }
    }

    private fun currentWindow(timing: Timing, now: Int): Window? {
        val fajr = minutesOf(timing.fajr)
        val zuhr = minutesOf(timing.zuhr)
        val asr = minutesOf(timing.asr)
        val maghrib = minutesOf(timing.maghrib)
        val isha = minutesOf(timing.isha)
        return when {
            now in fajr until minutesOf(timing.sunrise) -> Window(Prayer.FAJR, fajr, minutesOf(timing.sunrise))
            now in zuhr until asr -> Window(Prayer.ZUHR, zuhr, asr)
            now in asr until minutesOf(timing.sunset) -> Window(Prayer.ASR, asr, minutesOf(timing.sunset))
            now in maghrib until maghrib + MAGHRIB_MINUTES -> Window(Prayer.MAGHRIB, maghrib, maghrib + MAGHRIB_MINUTES)
            now >= isha -> Window(Prayer.ISHA, isha, fajr + MINUTES_IN_DAY)
            now < fajr -> Window(Prayer.ISHA, isha - MINUTES_IN_DAY, fajr)
            else -> null
        }
    }

    private fun nextWindow(timing: Timing, now: Int): Window {
        val sunrise = minutesOf(timing.sunrise)
        val sunset = minutesOf(timing.sunset)
        return when {
            now in sunrise until minutesOf(timing.zuhr) -> Window(Prayer.ZUHR, sunrise, minutesOf(timing.zuhr))
            now in sunset until minutesOf(timing.maghrib) -> Window(Prayer.MAGHRIB, sunset, minutesOf(timing.maghrib))
            else -> Window(Prayer.ISHA, minutesOf(timing.maghrib) + MAGHRIB_MINUTES, minutesOf(timing.isha))
        }
    }

    private fun Window.toHero(nowInSeconds: Int, isActive: Boolean): PrayerHero {
        val startInSeconds = start * SECONDS_IN_MINUTE
        val endInSeconds = end * SECONDS_IN_MINUTE
        val elapsed = (nowInSeconds - startInSeconds).toFloat() / (endInSeconds - startInSeconds).coerceAtLeast(1)
        return PrayerHero(
            prayer = prayer,
            isActive = isActive,
            startInMinutes = start.mod(MINUTES_IN_DAY),
            endInMinutes = end.mod(MINUTES_IN_DAY),
            secondsLeft = endInSeconds - nowInSeconds,
            progress = elapsed.coerceIn(0f, 1f),
        )
    }

    private fun minutesOf(time: String) = TimeUtils.timeToMinutes(time)
}
