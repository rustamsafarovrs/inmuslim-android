package tj.rsdevteam.inmuslim.ui.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.AnalyticsProperty
import tj.rsdevteam.inmuslim.analytics.AnalyticsTracker
import tj.rsdevteam.inmuslim.data.repositories.RegionRepository
import tj.rsdevteam.inmuslim.data.repositories.UserRepository
import javax.inject.Inject

/**
 * Created by Rustam Safarov on 8/20/23.
 * github.com/rustamsafarovrs
 */

@HiltViewModel
class LaunchViewModel
@Inject constructor(
    private val analytics: AnalyticsTracker,
    private val regionRepository: RegionRepository,
    private val userRepository: UserRepository,
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
            val regionId = regionRepository.getRegionId()
            identify(regionId)
            val openOnboarding = regionId <= 0
            analytics.log(AnalyticsEvent.AppLaunched(openOnboarding))
            _event.send(LaunchVMEvent.OpenMain(openOnboarding))
        }
    }

    /**
     * Re-attaches who this install is to every event of the session. Firebase keeps both values
     * itself, but a cold start is the one place that knows them even when nothing else changed.
     */
    private fun identify(regionId: Long) {
        analytics.setUserId(userRepository.getUserId().orNullIfUnset())
        analytics.setUserProperty(AnalyticsProperty.REGION_ID, regionId.orNullIfUnset())
    }

    /** Nothing is registered yet on a first run, and analytics expects `null` rather than `"-1"`. */
    private fun Long.orNullIfUnset(): String? = takeIf { it != UNSET_ID }?.toString()

    private companion object {

        /** The "unset" sentinel both stored ids use. */
        const val UNSET_ID = -1L
    }
}
