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

    fun getCurrentTimeInMinutes(): Int {
        val calendar = Calendar.getInstance()
        val h = calendar.get(Calendar.HOUR_OF_DAY)
        val m = calendar.get(Calendar.MINUTE)
        return h * 60 + m
    }

    data class PrayerInfo(
        val nameResId: Int,
        val startTime: String,
        val endTime: String,
        val startInMinutes: Int,
        val endInMinutes: Int
    )

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
        val m = timeToMinutes(timing.maghrib)
        val i = timeToMinutes(timing.isha)

        return when {
            now in f until sr -> PrayerInfo(fajrResId, timing.fajr, formatMinutes(sr - 1), f, sr - 1)
            now in z until a -> PrayerInfo(zuhrResId, timing.zuhr, formatMinutes(a - 1), z, a - 1)
            now in a until m -> PrayerInfo(asrResId, timing.asr, formatMinutes(m - 1), a, m - 1)
            now in m until (m + 40) -> PrayerInfo(maghribResId, timing.maghrib, formatMinutes(m + 39), m, m + 39)
            now >= i || now < f -> {
                val iMinutes = if (now < f) i - 24 * 60 else i
                PrayerInfo(ishaResId, timing.isha, formatMinutes(f - 1), iMinutes, f - 1)
            }
            else -> null
        }
    }

    private fun formatMinutes(minutes: Int): String {
        val h = (minutes / 60) % 24
        val m = minutes % 60
        return String.format(Locale.getDefault(), "%02d:%02d", h, m)
    }
}
