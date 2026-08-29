package tj.rsdevteam.inmuslim.feature.tasbih.data.db

/**
 * Projection of a [TasbihEntity] joined with its record for a given day.
 */
data class TasbihWithTodayCountEntity(
    val id: Long,
    val name: String,
    val todayCount: Int,
)
