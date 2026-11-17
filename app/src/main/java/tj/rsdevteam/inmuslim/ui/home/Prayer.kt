package tj.rsdevteam.inmuslim.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.res.R

enum class Prayer(
    @StringRes val nameResId: Int,
    @StringRes val arabicResId: Int,
    @DrawableRes val iconResId: Int,
) {
    FAJR(R.string.base_prayer_fajr, R.string.base_prayer_fajr_arabic, R.drawable.ic_fajr_24),
    SUNRISE(R.string.base_prayer_sunrise, R.string.base_prayer_sunrise_arabic, R.drawable.ic_sunrise_24),
    ZUHR(R.string.base_prayer_zuhr, R.string.base_prayer_zuhr_arabic, R.drawable.ic_zuhr_24),
    ASR(R.string.base_prayer_asr, R.string.base_prayer_asr_arabic, R.drawable.ic_asr_24),
    SUNSET(R.string.base_prayer_sunset, R.string.base_prayer_sunset_arabic, R.drawable.ic_sunset_24),
    MAGHRIB(R.string.base_prayer_maghrib, R.string.base_prayer_maghrib_arabic, R.drawable.ic_maghrib_24),
    ISHA(R.string.base_prayer_isha, R.string.base_prayer_isha_arabic, R.drawable.ic_isha_24),
}

enum class PrayerStatus { PAST, NOW, NEXT, FUTURE }

data class PrayerHero(
    val prayer: Prayer,
    val isActive: Boolean,
    val startInMinutes: Int,
    val endInMinutes: Int,
    val secondsLeft: Int,
    val progress: Float,
)

fun Timing.timeOf(prayer: Prayer): String = when (prayer) {
    Prayer.FAJR -> fajr
    Prayer.SUNRISE -> sunrise
    Prayer.ZUHR -> zuhr
    Prayer.ASR -> asr
    Prayer.SUNSET -> sunset
    Prayer.MAGHRIB -> maghrib
    Prayer.ISHA -> isha
}
