package tj.rsdevteam.inmuslim.ui.home

import tj.rsdevteam.inmuslim.data.models.ErrorBottomSheetConfig
import tj.rsdevteam.inmuslim.data.models.Timing

data class HomeScreenState(
    val errorBottomSheetConfig: ErrorBottomSheetConfig? = null,
    val showLoading: Boolean = false,
    val timing: Timing? = null,
    val isReviewShown: Boolean = false
)
