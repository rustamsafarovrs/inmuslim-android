package tj.rsdevteam.inmuslim.ui.region

import tj.rsdevteam.inmuslim.data.models.Region

sealed class RegionUIEvent {
    object GetRegions : RegionUIEvent()
    object DidClickConfirm : RegionUIEvent()
    data class DidSelectRegion(val region: Region) : RegionUIEvent()
    object DismissDialog : RegionUIEvent()
}
