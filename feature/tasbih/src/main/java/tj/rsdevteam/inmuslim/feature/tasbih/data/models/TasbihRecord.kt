package tj.rsdevteam.inmuslim.feature.tasbih.data.models

import tj.rsdevteam.inmuslim.core.TextRes

/**
 * One day of counting for a single tasbih.
 *
 * [date] is the raw ISO (`yyyy-MM-dd`) key; [dateLabel] is the text to display for it.
 */
data class TasbihRecord(
    val id: Long,
    val tasbihId: Long,
    val count: Int,
    val date: String,
    val dateLabel: TextRes,
)
