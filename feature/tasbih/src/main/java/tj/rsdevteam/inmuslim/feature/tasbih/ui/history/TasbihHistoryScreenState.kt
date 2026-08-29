package tj.rsdevteam.inmuslim.feature.tasbih.ui.history

import tj.rsdevteam.inmuslim.core.BaseState
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihDayHistory

data class TasbihHistoryScreenState(
    val base: BaseState = BaseState(),
    val days: List<TasbihDayHistory> = emptyList(),
) {
    val totalCount: Int get() = days.sumOf { it.total }

    val activeDays: Int get() = days.size

    val tasbihCount: Int get() = days.flatMap { day -> day.entries.map { it.tasbihId } }.distinct().size
}
