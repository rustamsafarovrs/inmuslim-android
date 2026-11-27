package tj.rsdevteam.inmuslim.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.analytics.AnalyticsEvent
import tj.rsdevteam.inmuslim.analytics.AnalyticsTracker
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.repositories.TimingRepository
import tj.rsdevteam.inmuslim.data.repositories.UserRepository
import tj.rsdevteam.inmuslim.utils.TimeUtils
import tj.rsdevteam.inmuslim.utils.Utils
import tj.rsdevteam.inmuslim.utils.WallClock
import tj.rsdevteam.inmuslim.utils.toErrorText
import tj.rstech.uicomponents.bottomsheet.error.ErrorBottomSheetConfig
import javax.inject.Inject

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val analytics: AnalyticsTracker,
    private val timingRepository: TimingRepository,
    private val userRepository: UserRepository,
    private val clock: WallClock,
) : ViewModel() {

    var state by mutableStateOf(HomeScreenState())
        private set

    private var clockJob: Job? = null

    // Null after a failed load: the next DidStart retries it, the clock doesn't
    private var timingDate: String? = null

    init {
        if (userRepository.getUserId() != -1L) {
            if (userRepository.getFirebaseToken().isEmpty()) {
                updateMessagingId()
            }
        }
        refresh()
    }

    private fun getTiming() {
        timingDate = today()
        viewModelScope.launch {
            val isFirstLoad = state.timing == null
            timingRepository.getTiming().collect { rs ->
                when (rs) {
                    is Resource.InProgress -> Unit
                    is Resource.Success -> {
                        state = state.copy(timing = rs.data)
                        updateSchedule()
                    }

                    is Resource.Error -> {
                        timingDate = null
                        analytics.log(AnalyticsEvent.TimingLoadFailed(rs.error?.message))
                        state = state.copy(
                            errorBottomSheetConfig = ErrorBottomSheetConfig(msg = rs.error.toErrorText()),
                        )
                    }
                }
                state = state.copy(showLoading = isFirstLoad && rs is Resource.InProgress)
            }
        }
    }

    private fun registerUser() {
        viewModelScope.launch {
            userRepository.registerUser(Utils.getDeviceInfo())
                .collect { rs ->
                    when (rs) {
                        is Resource.InProgress -> Unit
                        is Resource.Success -> {
                            analytics.setUserId(userRepository.getUserId().toString())
                            analytics.log(AnalyticsEvent.UserRegistered)
                            updateMessagingId()
                        }

                        is Resource.Error -> analytics.log(AnalyticsEvent.UserRegisterFailed(rs.error?.message))
                    }
                }
        }
    }

    private fun updateMessagingId() {
        viewModelScope.launch {
            userRepository.updateMessagingId()
                .collect { rs ->
                    when (rs) {
                        is Resource.InProgress -> Unit
                        is Resource.Success -> analytics.log(AnalyticsEvent.MessagingIdUpdated)
                        is Resource.Error -> analytics.log(AnalyticsEvent.MessagingIdUpdateFailed(rs.error?.message))
                    }
                }
        }
    }

    fun handleEvent(event: HomeUIEvent) {
        when (event) {
            is HomeUIEvent.Refresh -> refresh()
            is HomeUIEvent.ReviewShowed -> reviewShowed()
            is HomeUIEvent.DismissDialog -> dismissDialog()
            is HomeUIEvent.DidStart -> didStart()
            is HomeUIEvent.DidStop -> clockJob?.cancel()
        }
    }

    private fun refresh() {
        getTiming()
        if (userRepository.needRegister()) {
            registerUser()
        }
    }

    fun reviewShowed() {
        analytics.log(AnalyticsEvent.InAppReviewRequested)
        userRepository.saveReviewShown()
        state = state.copy(shouldRequestReview = false)
    }

    fun dismissDialog() {
        state = state.copy(errorBottomSheetConfig = null)
    }

    private fun didStart() {
        val today = today()
        userRepository.recordVisit(today)
        state = state.copy(shouldRequestReview = userRepository.shouldRequestReview())
        if (timingDate != today) {
            getTiming()
        }
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            while (isActive) {
                if (timingDate != null && timingDate != today()) {
                    getTiming()
                }
                updateSchedule()
                delay(MILLIS_IN_SECOND - clock.millis() % MILLIS_IN_SECOND)
            }
        }
    }

    private fun updateSchedule() {
        val timing = state.timing ?: return
        val now = TimeUtils.secondsOfDay(clock.millis())
        val hero = PrayerSchedule.heroAt(timing, now)
        val statuses = PrayerSchedule.statusesAt(timing, hero, now)
        // Same map instance lets HomeSchedule skip recomposing on every tick
        state = state.copy(hero = hero, statuses = if (statuses == state.statuses) state.statuses else statuses)
    }

    private fun today() = TimeUtils.isoDate(clock.millis())

    private companion object {
        const val MILLIS_IN_SECOND = 1_000L
    }
}
