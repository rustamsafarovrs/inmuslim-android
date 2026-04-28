package tj.rsdevteam.inmuslim.core.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Created by Rustam Safarov on 4/28/26.
 * github.com/rustamsafarovrs
 */

@Suppress("TooGenericExceptionCaught", "ReturnCount")
object DateUtils {

    private const val TAG = "DateUtils"

    const val ISO_DATE = "yyyy-MM-dd"
    const val ISO_TIME = "HH:mm:ss"
    const val ISO_DATE_TIME = "$ISO_DATE $ISO_TIME"
    const val ISO_T_DATE_TIME = "$ISO_DATE'T'$ISO_TIME"
    const val ISO_T_DATE_TIME_TIMEZONE = "$ISO_DATE'T'${ISO_TIME}XXX"

    const val HUMAN_DATE = "d MMMM yyyy"
    const val HUMAN_DAY_AND_MONTH = "d MMMM"
    const val HUMAN_MONTH_AND_YEAR = "MMMM yyyy"
    const val HUMAN_TIME = "HH:mm"
    const val HUMAN_DATE_TIME = "$HUMAN_DATE $HUMAN_TIME"
    const val LOG_DATE_TIME = "d MMM h:mm:ss a"

    fun formatDateTime(
        date: Date?,
        resultPattern: String = HUMAN_DATE_TIME,
        applyTimeZone: Boolean = false,
    ): String? {
        if (date == null) {
            return null
        }
        try {
            val outputForm = SimpleDateFormat(resultPattern, getLocale())
            if (applyTimeZone) {
                outputForm.timeZone = TimeZone.getDefault()
            }
            return outputForm.format(date)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return null
    }

    fun formatDateTime(
        dateTime: Long?,
        resultPattern: String = HUMAN_DATE_TIME,
        applyTimeZone: Boolean = false,
    ): String? {
        if (dateTime == null || dateTime == 0L) {
            return null
        }
        return formatDateTime(Date(dateTime), resultPattern, applyTimeZone)
    }

    fun formatDateTime(
        dateTime: String?,
        resultPattern: String = HUMAN_DATE_TIME,
        dateTimePattern: String = ISO_T_DATE_TIME,
        applyTimeZone: Boolean = false,
    ): String? {
        if (dateTime.isNullOrEmpty()) {
            return null
        }
        try {
            return formatDateTime(parseDate(dateTime, dateTimePattern, applyTimeZone), resultPattern)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return null
    }

    fun parseDate(
        date: String?,
        inputFormat: String = ISO_T_DATE_TIME,
        applyTimeZone: Boolean = false,
    ): Date? {
        if (date.isNullOrEmpty()) {
            return null
        }
        try {
            val inputFormat = SimpleDateFormat(inputFormat, getLocale())
            if (applyTimeZone) {
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            }
            return inputFormat.parse(date)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return null
    }

    @Suppress("MagicNumber")
    fun formatDuration(durationMs: Long): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        val hours = (durationMs / (1000 * 60 * 60))
        return buildString {
            if (hours > 0) append("$hours h ")
            if (minutes > 0 || hours > 0) append("$minutes min ")
            append("$seconds s")
        }.trim()
    }

    private fun getLocale(): Locale {
        return Locale.getDefault()
    }
}
