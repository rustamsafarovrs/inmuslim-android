package tj.rsdevteam.inmuslim.utils

import tj.rsdevteam.inmuslim.data.models.Timing
import java.util.Calendar
import java.util.Locale

object TimeUtils {

    fun timeToMinutes(time: String): Int {
        val parts = time.split(":")
        if (parts.size >= 2) {
            val h = parts[0].toIntOrNull() ?: 0
            val m = parts[1].toIntOrNull() ?: 0
            return h * 60 + m
        }
        return 0
    }

    fun formatTime(time: String, is24Hour: Boolean = true): String {
        val parts = time.split(":")
        if (parts.size >= 2) {
            val h = parts[0].toIntOrNull() ?: 0
            val m = parts[1].padStart(2, '0')
            return if (is24Hour) {
                "${h.toString().padStart(2, '0')}:$m"
            } else {
                val hour = if (h % 12 == 0) 12 else h % 12
                val period = if (h < 12) "AM" else "PM"
                "$hour:$m $period"
            }
        }
        return time
    }

    fun getCurrentTimeInMinutes(): Int {
        val calendar = Calendar.getInstance()
        val h = calendar.get(Calendar.HOUR_OF_DAY)
        val m = calendar.get(Calendar.MINUTE)
        return h * 60 + m
    }

    data class PrayerInfo(
        val nameResId: Int,
        val startTimeRaw: String,
        val startInMinutes: Int,
        val endInMinutes: Int
    )

    @Suppress("LongParameterList")
    fun findCurrentPrayer(
        timing: Timing,
        now: Int,
        fajrResId: Int,
        zuhrResId: Int,
        asrResId: Int,
        maghribResId: Int,
        ishaResId: Int
    ): PrayerInfo? {
        val f = timeToMinutes(timing.fajr)
        val sr = timeToMinutes(timing.sunrise)
        val z = timeToMinutes(timing.zuhr)
        val a = timeToMinutes(timing.asr)
        val s = timeToMinutes(timing.sunset)
        val m = timeToMinutes(timing.maghrib)
        val i = timeToMinutes(timing.isha)

        return when {
            now in f until sr -> PrayerInfo(fajrResId, timing.fajr, f, sr - 1)
            now in z until a -> PrayerInfo(zuhrResId, timing.zuhr, z, a - 1)
            now in a until s -> PrayerInfo(asrResId, timing.asr, a, s - 1)
            now in m until (m + 40) -> PrayerInfo(maghribResId, timing.maghrib, m, m + 39)
            now >= i || now < f -> {
                val iMinutes = if (now < f) i - 24 * 60 else i
                PrayerInfo(ishaResId, timing.isha, iMinutes, f - 1)
            }
            else -> null
        }
    }

    internal fun formatMinutes(minutes: Int, is24Hour: Boolean = true): String {
        val h = (minutes / 60) % 24
        val m = minutes % 60
        return if (is24Hour) {
            String.format(Locale.getDefault(), "%02d:%02d", h, m)
        } else {
            val hour = if (h % 12 == 0) 12 else h % 12
            val period = if (h < 12) "AM" else "PM"
            String.format(Locale.getDefault(), "%d:%02d %s", hour, m, period)
        }
    }
}
