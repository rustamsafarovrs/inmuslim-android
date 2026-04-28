package tj.rsdevteam.inmuslim.feature.tasbih.data.repositories

import kotlinx.coroutines.flow.Flow
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord

interface TasbihRepository {

    fun observeTasbihs(): Flow<Resource<List<Tasbih>>>

    fun observeHistory(tasbihId: Long): Flow<Resource<List<TasbihRecord>>>

    fun addTasbih(name: String): Flow<Resource<Long>>

    fun getTodayCount(tasbihId: Long): Flow<Resource<Int>>

    fun getTasbihName(tasbihId: Long): Flow<Resource<String>>

    fun increment(tasbihId: Long): Flow<Resource<Unit>>

    fun reset(tasbihId: Long): Flow<Resource<Unit>>

    fun isHapticEnabled(): Boolean

    fun setHapticEnabled(enabled: Boolean)
}
