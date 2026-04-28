package tj.rsdevteam.inmuslim.feature.tasbih.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.preferences.Preferences
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihDao
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihEntity
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihRecordEntity
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasbihRepositoryImpl @Inject constructor(
    private val dao: TasbihDao,
    private val preferences: Preferences,
) : TasbihRepository {

    private fun today(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    override fun observeTasbihs(): Flow<Resource<List<Tasbih>>> {
        return dao.observeAll()
            .map { entities ->
                val date = today()
                val list = entities.map { entity ->
                    val record = dao.getRecord(entity.id, date)
                    Tasbih(entity.id, entity.name, record?.count ?: 0)
                }
                Resource.Success(list) as Resource<List<Tasbih>>
            }
            .onStart { emit(Resource.InProgress()) }
            .catch { emit(Resource.Error(error = it)) }
    }

    override fun observeHistory(tasbihId: Long): Flow<Resource<List<TasbihRecord>>> {
        return dao.observeHistory(tasbihId)
            .map { records ->
                val list = records.map { TasbihRecord(it.id, it.tasbihId, it.count, it.date) }
                Resource.Success(list) as Resource<List<TasbihRecord>>
            }
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
        val count = dao.getRecord(tasbihId, today())?.count ?: 0
        emit(Resource.Success(count))
    }.catch { emit(Resource.Error(error = it)) }

    override fun getTasbihName(tasbihId: Long): Flow<Resource<String>> = flow {
        emit(Resource.InProgress())
        val name = dao.getById(tasbihId)?.name.orEmpty()
        emit(Resource.Success(name))
    }.catch { emit(Resource.Error(error = it)) }

    override fun increment(tasbihId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.InProgress())
        val date = today()
        val current = dao.getRecord(tasbihId, date)
        dao.upsertRecord(
            TasbihRecordEntity(
                id = current?.id ?: 0,
                tasbihId = tasbihId,
                count = (current?.count ?: 0) + 1,
                date = date,
            ),
        )
        emit(Resource.Success(Unit))
    }.catch { emit(Resource.Error(error = it)) }

    override fun reset(tasbihId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.InProgress())
        val current = dao.getRecord(tasbihId, today())
        if (current != null) {
            dao.upsertRecord(current.copy(count = 0))
        }
        emit(Resource.Success(Unit))
    }.catch { emit(Resource.Error(error = it)) }

    override fun isHapticEnabled(): Boolean = preferences.isHapticEnabled()

    override fun setHapticEnabled(enabled: Boolean) {
        preferences.setHapticEnabled(enabled)
    }
}
