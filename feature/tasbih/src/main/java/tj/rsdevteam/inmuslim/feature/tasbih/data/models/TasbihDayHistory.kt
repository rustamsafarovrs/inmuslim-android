package tj.rsdevteam.inmuslim.feature.tasbih.data.models

import tj.rsdevteam.inmuslim.core.TextRes

data class TasbihDayHistory(
    val date: String,
    val dateLabel: TextRes,
    val total: Int,
    val entries: List<TasbihHistoryEntry>,
)
