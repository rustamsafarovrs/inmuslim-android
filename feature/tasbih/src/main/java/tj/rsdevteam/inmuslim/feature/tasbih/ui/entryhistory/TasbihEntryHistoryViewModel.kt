package tj.rsdevteam.inmuslim.feature.tasbih.ui.entryhistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.core.BaseState
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.GetTasbihNameUseCase
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.ObserveEntryHistoryUseCase
import javax.inject.Inject

@HiltViewModel
class TasbihEntryHistoryViewModel @Inject constructor(
    private val observeEntryHistoryUseCase: ObserveEntryHistoryUseCase,
    private val getTasbihNameUseCase: GetTasbihNameUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val tasbihId = savedStateHandle.toRoute<Screen.TasbihEntryHistory>().tasbihId

    private val _state = MutableStateFlow(TasbihEntryHistoryScreenState(base = BaseState(isLoading = true)))
    val state = _state.asStateFlow()

    init {
        loadName()
        observeEntryHistory()
    }

    private fun loadName() {
        viewModelScope.launch {
            getTasbihNameUseCase(tasbihId).collect { rs ->
                if (rs is Resource.Success) _state.update { it.copy(tasbihName = rs.data) }
            }
        }
    }

    private fun observeEntryHistory() {
        viewModelScope.launch {
            observeEntryHistoryUseCase(tasbihId).collect { rs ->
                when (rs) {
                    is Resource.InProgress -> _state.update {
                        it.copy(base = it.base.copy(isLoading = true))
                    }

                    is Resource.Success -> _state.update {
                        it.copy(
                            records = rs.data,
                            base = it.base.copy(isLoading = false, error = null),
                        )
                    }

                    is Resource.Error -> _state.update {
                        it.copy(base = it.base.copy(isLoading = false, error = rs.error))
                    }
                }
            }
        }
    }
}
