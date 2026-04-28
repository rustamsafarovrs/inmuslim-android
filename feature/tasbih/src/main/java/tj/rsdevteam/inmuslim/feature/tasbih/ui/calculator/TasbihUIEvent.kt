package tj.rsdevteam.inmuslim.feature.tasbih.ui.calculator

sealed class TasbihUIEvent {
    object DidTap : TasbihUIEvent()
    object DidClickReset : TasbihUIEvent()
    object DidConfirmReset : TasbihUIEvent()
    object DidDismissResetConfirm : TasbihUIEvent()
    object DidToggleHaptic : TasbihUIEvent()
}
