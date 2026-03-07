package tj.rsdevteam.inmuslim.ui.launch

sealed class LaunchVMEvent {
    data class OpenMain(val openOnboarding: Boolean) : LaunchVMEvent()
}
