package tj.rsdevteam.inmuslim.feature.tasbih.ui.list

import tj.rsdevteam.inmuslim.core.BaseState
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.Tasbih

data class TasbihListScreenState(
    val base: BaseState = BaseState(),
    val tasbihs: List<Tasbih> = emptyList(),
    val showAddDialog: Boolean = false,
)
