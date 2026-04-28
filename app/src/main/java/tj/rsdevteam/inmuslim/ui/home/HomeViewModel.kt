package tj.rsdevteam.inmuslim.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.data.repositories.TimingRepository
import tj.rsdevteam.inmuslim.data.repositories.UserRepository
import tj.rsdevteam.inmuslim.res.R
import tj.rsdevteam.inmuslim.utils.TimeUtils
import tj.rsdevteam.inmuslim.utils.Utils
import tj.rstech.uicomponents.bottomsheet.error.ErrorBottomSheetConfig
import javax.inject.Inject

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val timingRepository: TimingRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    var state by mutableStateOf(HomeScreenState())
        private set

    private var prayerEndJob: Job? = null

    init {
        state = state.copy(isReviewShown = userRepository.isReviewShown())
        if (userRepository.getUserId() != -1L) {
            if (userRepository.getFirebaseToken().isEmpty()) {
                updateMessagingId()
            }
        }
        refresh()
    }

    override fun onCleared() {
        super.onCleared()
        prayerEndJob?.cancel()
    }

    private fun getTiming() {
        viewModelScope.launch {
            val isFirstLoad = state.timing == null
            timingRepository.getTiming().collect { rs ->
                when (rs) {
                    is Resource.InProgress -> Unit
                    is Resource.Success -> {
                        state = state.copy(timing = rs.data)
                        calculateCurrentPrayer()
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            errorBottomSheetConfig = ErrorBottomSheetConfig(
                                msg = rs.error?.message,
                                title = "Error",
                            ),
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
                        is Resource.Success -> updateMessagingId()
                        is Resource.Error -> Unit
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
                        is Resource.Success -> Unit
                        is Resource.Error -> Unit
                    }
                }
        }
    }

    fun handleEvent(event: HomeUIEvent) {
        when (event) {
            is HomeUIEvent.Refresh -> refresh()
            is HomeUIEvent.ReviewShowed -> reviewShowed()
            is HomeUIEvent.DismissDialog -> dismissDialog()
        }
    }

    private fun refresh() {
        getTiming()
        if (userRepository.needRegister()) {
            registerUser()
        }
    }

    fun reviewShowed() {
        userRepository.saveReviewShown()
        state = state.copy(isReviewShown = true)
    }

    fun dismissDialog() {
        state = state.copy(errorBottomSheetConfig = null)
    }

    private fun calculateCurrentPrayer() {
        val timing = state.timing ?: return
        val now = TimeUtils.getCurrentTimeInMinutes()

        val info = TimeUtils.findCurrentPrayer(
            timing = timing,
            now = now,
            fajrResId = R.string.base_prayer_fajr,
            zuhrResId = R.string.base_prayer_zuhr,
            asrResId = R.string.base_prayer_asr,
            maghribResId = R.string.base_prayer_maghrib,
            ishaResId = R.string.base_prayer_isha,
        )

        if (info != null) {
            val total = if (info.endInMinutes > info.startInMinutes) {
                info.endInMinutes - info.startInMinutes
            } else {
                (info.endInMinutes + 24 * 60) - info.startInMinutes
            }
            val currentNow = if (now < info.startInMinutes && info.endInMinutes <= info.startInMinutes) {
                now + 24 * 60
            } else {
                now
            }
            val progress = (currentNow - info.startInMinutes).toFloat() / total
            state = state.copy(
                currentPrayer = ActivePrayer(
                    nameResId = info.nameResId,
                    startTimeRaw = info.startTimeRaw,
                    endInMinutes = info.endInMinutes,
                    progress = progress.coerceIn(0f, 1f),
                ),
            )
        } else {
            state = state.copy(currentPrayer = null)
        }

        schedulePrayerEndRefresh()
    }

    private fun schedulePrayerEndRefresh() {
        prayerEndJob?.cancel()
        val endInMinutes = state.currentPrayer?.endInMinutes ?: return
        val now = TimeUtils.getCurrentTimeInMinutes()
        val delayMinutes = if (endInMinutes >= now) {
            endInMinutes + 1 - now
        } else {
            endInMinutes + 24 * 60 + 1 - now
        }
        prayerEndJob = viewModelScope.launch {
            delay(delayMinutes * 60_000L)
            getTiming()
        }
    }
}
