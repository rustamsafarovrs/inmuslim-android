package tj.rsdevteam.inmuslim.feature.tasbih.fakes

import kotlinx.coroutines.flow.Flow
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihDayHistory
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository

/** Answers with whatever a test assigns; a call nobody configured fails with "unused". */
class FakeTasbihRepository : TasbihRepository {

    var tasbihs: Flow<Resource<List<Tasbih>>>? = null
    var entryHistory: Flow<Resource<List<TasbihRecord>>>? = null
    var history: Flow<Resource<List<TasbihDayHistory>>>? = null
    var addResult: Flow<Resource<Long>>? = null
    var todayCount: Flow<Resource<Int>>? = null
    var tasbihName: Flow<Resource<String>>? = null
    var hapticEnabled: Boolean? = null

    val addedNames = mutableListOf<String>()

    override fun observeTasbihs(): Flow<Resource<List<Tasbih>>> = tasbihs ?: unused()

    override fun observeEntryHistory(tasbihId: Long): Flow<Resource<List<TasbihRecord>>> = entryHistory ?: unused()

    override fun observeHistory(): Flow<Resource<List<TasbihDayHistory>>> = history ?: unused()

    override fun addTasbih(name: String): Flow<Resource<Long>> {
        addedNames += name
        return addResult ?: unused()
    }

    override fun getTodayCount(tasbihId: Long): Flow<Resource<Int>> = todayCount ?: unused()

    override fun getTasbihName(tasbihId: Long): Flow<Resource<String>> = tasbihName ?: unused()

    override fun increment(tasbihId: Long): Flow<Resource<Unit>> = unused()

    override fun reset(tasbihId: Long): Flow<Resource<Unit>> = unused()

    override fun isHapticEnabled(): Boolean = hapticEnabled ?: unused()

    override fun setHapticEnabled(enabled: Boolean) {
        hapticEnabled = enabled
    }

    private fun unused(): Nothing = error("unused")
}
