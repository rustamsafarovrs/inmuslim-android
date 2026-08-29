package tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases

import kotlinx.coroutines.flow.Flow
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import javax.inject.Inject

class AddTasbihUseCase @Inject constructor(
    private val repository: TasbihRepository,
) {

    operator fun invoke(name: String): Flow<Resource<Long>> {
        return repository.addTasbih(name)
    }
}
