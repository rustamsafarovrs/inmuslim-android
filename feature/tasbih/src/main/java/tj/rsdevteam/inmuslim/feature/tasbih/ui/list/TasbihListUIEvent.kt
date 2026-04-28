package tj.rsdevteam.inmuslim.feature.tasbih.ui.list

sealed class TasbihListUIEvent {
    object DidClickShowAddDialog : TasbihListUIEvent()
    object DidDismissAddDialog : TasbihListUIEvent()
    data class DidClickAdd(val name: String) : TasbihListUIEvent()
}
