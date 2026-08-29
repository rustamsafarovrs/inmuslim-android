package tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases

import kotlinx.coroutines.flow.Flow
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import javax.inject.Inject

class ObserveTasbihsUseCase @Inject constructor(
    private val repository: TasbihRepository,
) {

    operator fun invoke(): Flow<Resource<List<Tasbih>>> {
        return repository.observeTasbihs()
    }
}
