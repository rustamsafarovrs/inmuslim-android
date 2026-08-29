package tj.rsdevteam.inmuslim.feature.tasbih.ui.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.GetTasbihUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.IncrementTasbihUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.IsHapticEnabledUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.ResetTasbihUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.SetHapticEnabledUseCase
import javax.inject.Inject

@HiltViewModel
class TasbihViewModel @Inject constructor(
    private val getTasbihUseCase: GetTasbihUseCase,
    private val incrementTasbihUseCase: IncrementTasbihUseCase,
    private val resetTasbihUseCase: ResetTasbihUseCase,
    private val setHapticEnabledUseCase: SetHapticEnabledUseCase,
    isHapticEnabledUseCase: IsHapticEnabledUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val tasbihId = savedStateHandle.toRoute<Screen.TasbihCalculator>().tasbihId

    var state by mutableStateOf(TasbihScreenState(hapticEnabled = isHapticEnabledUseCase()))
        private set

    init {
        loadTasbih()
    }

    private fun loadTasbih() {
        viewModelScope.launch {
            getTasbihUseCase(tasbihId).collect { rs ->
                if (rs is Resource.Success) {
                    state = state.copy(name = rs.data.name, count = rs.data.todayCount)
                }
            }
        }
    }

    fun handleEvent(event: TasbihUIEvent) {
        when (event) {
            is TasbihUIEvent.DidTap -> handleTap()
            is TasbihUIEvent.DidClickReset -> state = state.copy(showResetConfirm = true)
            is TasbihUIEvent.DidDismissResetConfirm -> state = state.copy(showResetConfirm = false)
            is TasbihUIEvent.DidConfirmReset -> handleReset()
            is TasbihUIEvent.DidToggleHaptic -> handleToggleHaptic()
        }
    }

    private fun handleToggleHaptic() {
        val enabled = !state.hapticEnabled
        setHapticEnabledUseCase(enabled)
        state = state.copy(hapticEnabled = enabled)
    }

    private fun handleTap() {
        state = state.copy(count = state.count + 1)
        viewModelScope.launch { incrementTasbihUseCase(tasbihId).collect {} }
    }

    private fun handleReset() {
        state = state.copy(count = 0, showResetConfirm = false)
        viewModelScope.launch { resetTasbihUseCase(tasbihId).collect {} }
    }
}
