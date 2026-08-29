package tj.rsdevteam.inmuslim.feature.tasbih.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.core.BaseState
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.domain.usecases.ObserveHistoryUseCase
import javax.inject.Inject

@HiltViewModel
class TasbihHistoryViewModel @Inject constructor(
    private val observeHistoryUseCase: ObserveHistoryUseCase,
) : ViewModel() {

    var state by mutableStateOf(TasbihHistoryScreenState(base = BaseState(isLoading = true)))
        private set

    init {
        observeHistory()
    }

    private fun observeHistory() = viewModelScope.launch {
        observeHistoryUseCase().collect { rs ->
            state = when (rs) {
                is Resource.InProgress -> state.copy(base = state.base.copy(isLoading = true))

                is Resource.Success -> state.copy(
                    days = rs.data,
                    base = state.base.copy(isLoading = false, error = null),
                )

                is Resource.Error -> state.copy(
                    base = state.base.copy(isLoading = false, error = rs.error),
                )
            }
        }
    }
}
