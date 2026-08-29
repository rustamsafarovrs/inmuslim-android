package tj.rsdevteam.inmuslim.ui.settings

sealed class SettingsUIEvent {
    object DidClickRegion : SettingsUIEvent()
    object DidClickLanguage : SettingsUIEvent()
}
