package tj.rsdevteam.inmuslim.utils

import java.util.Calendar
import java.util.Locale

object TimeUtils {

    private const val SECONDS_IN_MINUTE = 60
    private const val SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE

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

    fun getCurrentTimeInSeconds(): Int {
        val calendar = Calendar.getInstance()
        val h = calendar.get(Calendar.HOUR_OF_DAY)
        val m = calendar.get(Calendar.MINUTE)
        val s = calendar.get(Calendar.SECOND)
        return h * SECONDS_IN_HOUR + m * SECONDS_IN_MINUTE + s
    }

    internal fun formatCountdown(seconds: Int): String {
        val total = seconds.coerceAtLeast(0)
        val h = total / SECONDS_IN_HOUR
        val m = total % SECONDS_IN_HOUR / SECONDS_IN_MINUTE
        val s = total % SECONDS_IN_MINUTE
        return if (h > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", h, m, s)
        } else {
            String.format(Locale.getDefault(), "%d:%02d", m, s)
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
