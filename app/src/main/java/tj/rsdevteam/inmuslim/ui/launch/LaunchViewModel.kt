package tj.rsdevteam.inmuslim.ui.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.data.repositories.RegionRepository
import javax.inject.Inject

/**
 * Created by Rustam Safarov on 8/20/23.
 * github.com/rustamsafarovrs
 */

@HiltViewModel
class LaunchViewModel
@Inject constructor(
    private val regionRepository: RegionRepository,
) : ViewModel() {

    private val _event = Channel<LaunchVMEvent>()
    val event = _event.receiveAsFlow()

    init {
        handleEvent(LaunchUIEvent.Init)
    }

    fun handleEvent(event: LaunchUIEvent) {
        when (event) {
            is LaunchUIEvent.Init -> {
                checkRegion()
            }
        }
    }

    private fun checkRegion() {
        viewModelScope.launch {
            if (regionRepository.getRegionId() > 0) {
                _event.send(LaunchVMEvent.OpenMain(false))
            } else {
                _event.send(LaunchVMEvent.OpenMain(true))
            }
        }
    }
}
