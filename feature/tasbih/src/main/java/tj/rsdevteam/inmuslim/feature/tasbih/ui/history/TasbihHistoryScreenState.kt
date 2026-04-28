package tj.rsdevteam.inmuslim.feature.tasbih.ui.history

import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord

data class TasbihHistoryScreenState(
    val tasbihName: String = "",
    val records: List<TasbihRecord> = emptyList(),
)
