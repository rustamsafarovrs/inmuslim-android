package tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases

import kotlinx.coroutines.flow.Flow
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihDayHistory
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import javax.inject.Inject

class ObserveHistoryUseCase @Inject constructor(
    private val repository: TasbihRepository,
) {

    operator fun invoke(): Flow<Resource<List<TasbihDayHistory>>> {
        return repository.observeHistory()
    }
}
