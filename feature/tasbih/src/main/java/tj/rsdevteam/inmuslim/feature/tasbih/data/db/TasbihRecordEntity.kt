package tj.rsdevteam.inmuslim.feature.tasbih.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasbih_records",
    foreignKeys = [
        ForeignKey(
            entity = TasbihEntity::class,
            parentColumns = ["id"],
            childColumns = ["tasbihId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("tasbihId")],
)
data class TasbihRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tasbihId: Long,
    val count: Int,
    val date: String,
)
