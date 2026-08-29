package tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases

import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import javax.inject.Inject

class IsHapticEnabledUseCase @Inject constructor(
    private val repository: TasbihRepository,
) {

    operator fun invoke(): Boolean {
        return repository.isHapticEnabled()
    }
}
