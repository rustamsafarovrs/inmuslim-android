package tj.rsdevteam.inmuslim.feature.tasbih.data.repositories

import tj.rsdevteam.inmuslim.core.TextRes
import tj.rsdevteam.inmuslim.core.asTextRes
import tj.rsdevteam.inmuslim.core.textResId
import tj.rsdevteam.inmuslim.core.utils.DateUtils
import tj.rsdevteam.inmuslim.feature.tasbih.data.RelativeDay
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihRecordEntity
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihRecordWithNameEntity
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihWithTodayCountEntity
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihDayHistory
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihHistoryEntry
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord
import tj.rsdevteam.inmuslim.feature.tasbih.data.resolveRelativeDay
import tj.rsdevteam.inmuslim.res.R
import java.util.Date

internal fun formatDay(isoDate: String, now: Date = Date()): TextRes {
    return when (resolveRelativeDay(isoDate, now)) {
        RelativeDay.TODAY -> textResId(R.string.common_other_today)
        RelativeDay.YESTERDAY -> textResId(R.string.common_other_yesterday)
        RelativeDay.OTHER -> DateUtils.formatDateTime(
            isoDate,
            resultPattern = DateUtils.HUMAN_DATE,
            dateTimePattern = DateUtils.ISO_DATE,
        ).orEmpty().asTextRes()
    }
}

internal fun toTasbih(entity: TasbihWithTodayCountEntity): Tasbih {
    return Tasbih(
        id = entity.id,
        name = entity.name,
        todayCount = entity.todayCount,
    )
}

internal fun toRecord(entity: TasbihRecordEntity, dateLabel: TextRes): TasbihRecord {
    return TasbihRecord(
        id = entity.id,
        tasbihId = entity.tasbihId,
        count = entity.count,
        date = entity.date,
        dateLabel = dateLabel,
    )
}

internal fun TasbihRecordWithNameEntity.toEntry(dateLabel: TextRes): TasbihHistoryEntry {
    return TasbihHistoryEntry(
        tasbihId = tasbihId,
        tasbihName = tasbihName,
        count = count,
        date = date,
        dateLabel = dateLabel,
    )
}

internal fun List<TasbihRecordWithNameEntity>.toDayHistory(now: Date = Date()): List<TasbihDayHistory> {
    return groupBy { it.date }
        .map { (date, dayRecords) ->
            val dateLabel = formatDay(date, now)
            TasbihDayHistory(
                date = date,
                dateLabel = dateLabel,
                total = dayRecords.sumOf { it.count },
                entries = dayRecords.sortedByDescending { it.count }.map { it.toEntry(dateLabel) },
            )
        }
        .sortedByDescending { it.date }
}
