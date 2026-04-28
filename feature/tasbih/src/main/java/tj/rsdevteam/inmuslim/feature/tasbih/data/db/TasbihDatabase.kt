package tj.rsdevteam.inmuslim.feature.tasbih.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TasbihEntity::class, TasbihRecordEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class TasbihDatabase : RoomDatabase() {
    abstract fun tasbihDao(): TasbihDao
}
