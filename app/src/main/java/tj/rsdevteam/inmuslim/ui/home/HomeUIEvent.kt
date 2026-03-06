package tj.rsdevteam.inmuslim.ui.home

sealed class HomeUIEvent {
    object Refresh : HomeUIEvent()
    object ReviewShowed : HomeUIEvent()
    object DismissDialog : HomeUIEvent()
}
