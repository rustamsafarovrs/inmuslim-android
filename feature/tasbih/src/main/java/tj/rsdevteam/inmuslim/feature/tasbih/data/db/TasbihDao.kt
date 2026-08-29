package tj.rsdevteam.inmuslim.feature.tasbih.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TasbihDao {

    @Query(
        """
        SELECT t.id AS id, t.name AS name, IFNULL(SUM(r.count), 0) AS todayCount
        FROM tasbihs t
        LEFT JOIN tasbih_records r ON r.tasbihId = t.id AND r.date = :date
        GROUP BY t.id
        ORDER BY t.id DESC
        """,
    )
    fun observeAllWithCountFor(date: String): Flow<List<TasbihWithTodayCountEntity>>

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

    @Query("SELECT * FROM tasbih_records WHERE tasbihId = :tasbihId AND count > 0 ORDER BY date DESC")
    fun observeEntryHistory(tasbihId: Long): Flow<List<TasbihRecordEntity>>

    @Query(
        """
        SELECT r.tasbihId AS tasbihId, t.name AS tasbihName, r.count AS count, r.date AS date
        FROM tasbih_records r
        INNER JOIN tasbihs t ON t.id = r.tasbihId
        WHERE r.count > 0
        ORDER BY r.date DESC, r.count DESC
        """,
    )
    fun observeHistory(): Flow<List<TasbihRecordWithNameEntity>>
}
