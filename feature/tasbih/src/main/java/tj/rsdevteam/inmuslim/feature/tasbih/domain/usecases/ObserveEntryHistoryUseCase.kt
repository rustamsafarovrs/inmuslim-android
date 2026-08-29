package tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases

import kotlinx.coroutines.flow.Flow
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import javax.inject.Inject

class ObserveEntryHistoryUseCase @Inject constructor(
    private val repository: TasbihRepository,
) {

    operator fun invoke(tasbihId: Long): Flow<Resource<List<TasbihRecord>>> {
        return repository.observeEntryHistory(tasbihId)
    }
}
