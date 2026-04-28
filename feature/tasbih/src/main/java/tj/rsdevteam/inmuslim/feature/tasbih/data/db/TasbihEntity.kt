package tj.rsdevteam.inmuslim.feature.tasbih.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasbihs")
data class TasbihEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)
