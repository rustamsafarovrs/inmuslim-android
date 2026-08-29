package tj.rsdevteam.inmuslim.feature.tasbih.data.db

/**
 * Projection of a [TasbihRecordEntity] joined with the name of its parent [TasbihEntity].
 */
data class TasbihRecordWithNameEntity(
    val tasbihId: Long,
    val tasbihName: String,
    val count: Int,
    val date: String,
)
