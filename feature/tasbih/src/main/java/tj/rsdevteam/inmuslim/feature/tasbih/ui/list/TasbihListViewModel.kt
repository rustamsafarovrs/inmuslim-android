package tj.rsdevteam.inmuslim.feature.tasbih.ui.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.rsdevteam.inmuslim.core.Resource
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import javax.inject.Inject

@HiltViewModel
class TasbihListViewModel @Inject constructor(
    private val repository: TasbihRepository,
) : ViewModel() {

    var state by mutableStateOf(TasbihListScreenState())
        private set

    init {
        observeTasbihs()
    }

    private fun observeTasbihs() {
        viewModelScope.launch {
            repository.observeTasbihs().collect { rs ->
                state = when (rs) {
                    is Resource.InProgress -> state.copy(base = state.base.copy(isLoading = true))
                    is Resource.Success -> state.copy(
                        tasbihs = rs.data,
                        base = state.base.copy(isLoading = false, error = null),
                    )
                    is Resource.Error -> state.copy(
                        base = state.base.copy(isLoading = false, error = rs.error),
                    )
                }
            }
        }
    }

    fun handleEvent(event: TasbihListUIEvent) {
        when (event) {
            is TasbihListUIEvent.DidClickShowAddDialog -> state = state.copy(showAddDialog = true)
            is TasbihListUIEvent.DidDismissAddDialog -> state = state.copy(showAddDialog = false)
            is TasbihListUIEvent.DidClickAdd -> addTasbih(event.name)
        }
    }

    private fun addTasbih(name: String) {
        state = state.copy(showAddDialog = false)
        viewModelScope.launch {
            repository.addTasbih(name).collect { rs ->
                if (rs is Resource.Error) {
                    state = state.copy(base = state.base.copy(error = rs.error))
                }
            }
        }
    }
}
