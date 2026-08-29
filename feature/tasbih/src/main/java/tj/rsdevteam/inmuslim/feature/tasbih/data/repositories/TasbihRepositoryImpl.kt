package tj.rsdevteam.inmuslim.feature.tasbih.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.preferences.Preferences
import tj.rsdevteam.inmuslim.feature.tasbih.data.currentIsoDate
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihDao
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihDatabase
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihEntity
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihRecordEntity
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihDayHistory
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasbihRepositoryImpl @Inject constructor(
    private val dao: TasbihDao,
    private val database: TasbihDatabase,
    private val preferences: Preferences,
) : TasbihRepository {

    override fun observeTasbihs(): Flow<Resource<List<Tasbih>>> {
        return dao.observeAllWithCountFor(currentIsoDate())
            .map { entities -> Resource.Success(entities.map { toTasbih(it) }) as Resource<List<Tasbih>> }
            .onStart { emit(Resource.InProgress()) }
            .catch { emit(Resource.Error(error = it)) }
    }

    override fun observeEntryHistory(tasbihId: Long): Flow<Resource<List<TasbihRecord>>> {
        return dao.observeEntryHistory(tasbihId)
            .map { records ->
                val list = records.map { toRecord(it, formatDay(it.date)) }
                Resource.Success(list) as Resource<List<TasbihRecord>>
            }
            .onStart { emit(Resource.InProgress()) }
            .catch { emit(Resource.Error(error = it)) }
    }

    override fun observeHistory(): Flow<Resource<List<TasbihDayHistory>>> {
        return dao.observeHistory()
            .map { records -> Resource.Success(records.toDayHistory()) as Resource<List<TasbihDayHistory>> }
            .onStart { emit(Resource.InProgress()) }
            .catch { emit(Resource.Error(error = it)) }
    }

    override fun addTasbih(name: String): Flow<Resource<Long>> = flow {
        emit(Resource.InProgress())
        val id = dao.insert(TasbihEntity(name = name))
        emit(Resource.Success(id))
    }.catch { emit(Resource.Error(error = it)) }

    override fun getTodayCount(tasbihId: Long): Flow<Resource<Int>> = flow {
        emit(Resource.InProgress())
        val count = dao.getRecord(tasbihId, currentIsoDate())?.count ?: 0
        emit(Resource.Success(count))
    }.catch { emit(Resource.Error(error = it)) }

    override fun getTasbihName(tasbihId: Long): Flow<Resource<String>> = flow {
        emit(Resource.InProgress())
        val name = dao.getById(tasbihId)?.name.orEmpty()
        emit(Resource.Success(name))
    }.catch { emit(Resource.Error(error = it)) }

    override fun increment(tasbihId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.InProgress())
        database.withTransaction {
            val date = currentIsoDate()
            val current = dao.getRecord(tasbihId, date)
            dao.upsertRecord(
                TasbihRecordEntity(
                    id = current?.id ?: 0,
                    tasbihId = tasbihId,
                    count = (current?.count ?: 0) + 1,
                    date = date,
                ),
            )
        }
        emit(Resource.Success(Unit))
    }.catch { emit(Resource.Error(error = it)) }

    override fun reset(tasbihId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.InProgress())
        database.withTransaction {
            val current = dao.getRecord(tasbihId, currentIsoDate())
            if (current != null) {
                dao.upsertRecord(current.copy(count = 0))
            }
        }
        emit(Resource.Success(Unit))
    }.catch { emit(Resource.Error(error = it)) }

    override fun isHapticEnabled(): Boolean {
        return preferences.isHapticEnabled()
    }

    override fun setHapticEnabled(enabled: Boolean) {
        preferences.setHapticEnabled(enabled)
    }
}
