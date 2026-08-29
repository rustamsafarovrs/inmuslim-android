package tj.rsdevteam.inmuslim.feature.tasbih.ui.entryhistory

import tj.rsdevteam.inmuslim.core.BaseState
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord

data class TasbihEntryHistoryScreenState(
    val base: BaseState = BaseState(),
    val tasbihName: String = "",
    val records: List<TasbihRecord> = emptyList(),
) {
    val totalCount: Int get() = records.sumOf { it.count }

    val activeDays: Int get() = records.size

    val bestCount: Int get() = records.maxOfOrNull { it.count } ?: 0
}
