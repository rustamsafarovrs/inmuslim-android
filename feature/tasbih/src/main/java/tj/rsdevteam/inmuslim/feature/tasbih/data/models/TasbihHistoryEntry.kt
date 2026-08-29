package tj.rsdevteam.inmuslim.feature.tasbih.data.models

import tj.rsdevteam.inmuslim.core.TextRes

/**
 * A single tasbih counted on a given day.
 *
 * [date] is the raw ISO (`yyyy-MM-dd`) key; [dateLabel] is the text to display for it.
 */
data class TasbihHistoryEntry(
    val tasbihId: Long,
    val tasbihName: String,
    val count: Int,
    val date: String,
    val dateLabel: TextRes,
)
