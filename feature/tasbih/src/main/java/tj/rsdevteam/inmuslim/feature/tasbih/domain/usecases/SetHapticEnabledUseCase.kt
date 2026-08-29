package tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases

import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import javax.inject.Inject

class SetHapticEnabledUseCase @Inject constructor(
    private val repository: TasbihRepository,
) {

    operator fun invoke(enabled: Boolean) {
        repository.setHapticEnabled(enabled)
    }
}
