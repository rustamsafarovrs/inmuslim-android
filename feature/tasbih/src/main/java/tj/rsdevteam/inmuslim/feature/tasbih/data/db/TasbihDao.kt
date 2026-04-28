package tj.rsdevteam.inmuslim.feature.tasbih.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TasbihDao {

    @Query("SELECT * FROM tasbihs ORDER BY id DESC")
    fun observeAll(): Flow<List<TasbihEntity>>

    @Insert
    suspend fun insert(entity: TasbihEntity): Long

    @Delete
    suspend fun delete(entity: TasbihEntity)

    @Query("SELECT * FROM tasbihs WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TasbihEntity?

    @Query("SELECT * FROM tasbih_records WHERE tasbihId = :tasbihId AND date = :date LIMIT 1")
    suspend fun getRecord(tasbihId: Long, date: String): TasbihRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: TasbihRecordEntity)

    @Query("SELECT * FROM tasbih_records WHERE tasbihId = :tasbihId ORDER BY date DESC")
    fun observeHistory(tasbihId: Long): Flow<List<TasbihRecordEntity>>
}
