package tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import javax.inject.Inject

class GetTasbihUseCase @Inject constructor(
    private val repository: TasbihRepository,
) {

    operator fun invoke(tasbihId: Long): Flow<Resource<Tasbih>> {
        return combine(
            repository.getTasbihName(tasbihId),
            repository.getTodayCount(tasbihId),
        ) { name, count ->
            merge(tasbihId, name, count)
        }
    }

    private fun merge(tasbihId: Long, name: Resource<String>, count: Resource<Int>): Resource<Tasbih> {
        return when {
            name is Resource.Error -> Resource.Error(error = name.error)
            count is Resource.Error -> Resource.Error(error = count.error)
            name is Resource.Success && count is Resource.Success ->
                Resource.Success(Tasbih(tasbihId, name.data, count.data))

            else -> Resource.InProgress()
        }
    }
}
