package tj.rsdevteam.inmuslim.feature.tasbih.data

import tj.rsdevteam.inmuslim.core.utils.DateUtils
import java.util.Calendar
import java.util.Date

internal fun currentIsoDate(): String {
    return isoDateDaysAgo(Date(), days = 0)
}

internal fun resolveRelativeDay(isoDate: String, now: Date = Date()): RelativeDay {
    return when (isoDate) {
        isoDateDaysAgo(now, days = 0) -> RelativeDay.TODAY
        isoDateDaysAgo(now, days = 1) -> RelativeDay.YESTERDAY
        else -> RelativeDay.OTHER
    }
}

private fun isoDateDaysAgo(from: Date, days: Int): String {
    val calendar = Calendar.getInstance().apply {
        time = from
        add(Calendar.DAY_OF_YEAR, -days)
    }
    return DateUtils.formatDateTime(calendar.time, DateUtils.ISO_DATE).orEmpty()
}
