package tj.rsdevteam.inmuslim.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.data.models.ErrorBottomSheetConfig
import tj.rsdevteam.inmuslim.data.models.Resource
import tj.rsdevteam.inmuslim.data.repositories.TimingRepository
import tj.rsdevteam.inmuslim.data.repositories.UserRepository
import tj.rsdevteam.inmuslim.res.R
import tj.rsdevteam.inmuslim.utils.TimeUtils
import tj.rsdevteam.inmuslim.utils.Utils
import javax.inject.Inject

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val timingRepository: TimingRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var state by mutableStateOf(HomeScreenState())
        private set

    init {
        state = state.copy(isReviewShown = userRepository.isReviewShown())
        if (userRepository.getUserId() != -1L) {
            if (userRepository.getFirebaseToken().isEmpty()) {
                updateMessagingId()
            }
        }
        refresh()
    }

    private fun getTiming() {
        viewModelScope.launch {
            timingRepository.getTiming()
                .collect { rs ->
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
                                )
                            )
                        }
                    }
                    state = state.copy(showLoading = rs is Resource.InProgress)
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
            fajrResId = R.string.fajr,
            zuhrResId = R.string.zuhr,
            asrResId = R.string.asr,
            maghribResId = R.string.maghrib,
            ishaResId = R.string.isha
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
                    progress = progress.coerceIn(0f, 1f)
                )
            )
        } else {
            state = state.copy(currentPrayer = null)
        }
    }
}
