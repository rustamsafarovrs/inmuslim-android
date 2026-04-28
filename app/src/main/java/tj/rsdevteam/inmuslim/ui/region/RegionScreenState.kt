package tj.rsdevteam.inmuslim.ui.region

import tj.rsdevteam.inmuslim.data.models.Region
import tj.rstech.uicomponents.bottomsheet.error.ErrorBottomSheetConfig

data class RegionScreenState(
    val list: List<Region> = emptyList(),
    val showLoading: Boolean = false,
    val sheetConfig: ErrorBottomSheetConfig? = null,
)
