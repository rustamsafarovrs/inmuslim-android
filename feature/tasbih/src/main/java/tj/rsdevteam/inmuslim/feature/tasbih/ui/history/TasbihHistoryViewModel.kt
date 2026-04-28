package tj.rsdevteam.inmuslim.feature.tasbih.ui.history

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
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import javax.inject.Inject

@HiltViewModel
class TasbihHistoryViewModel @Inject constructor(
    private val repository: TasbihRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val tasbihId = savedStateHandle.toRoute<Screen.TasbihHistory>().tasbihId

    var state by mutableStateOf(TasbihHistoryScreenState())
        private set

    init {
        loadName()
        observeHistory()
    }

    private fun loadName() {
        viewModelScope.launch {
            repository.getTasbihName(tasbihId).collect { rs ->
                if (rs is Resource.Success) state = state.copy(tasbihName = rs.data)
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.observeHistory(tasbihId).collect { rs ->
                if (rs is Resource.Success) state = state.copy(records = rs.data)
            }
        }
    }
}
