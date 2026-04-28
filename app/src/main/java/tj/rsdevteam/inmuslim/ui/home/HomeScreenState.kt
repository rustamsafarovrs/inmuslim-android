package tj.rsdevteam.inmuslim.ui.home

import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rstech.uicomponents.bottomsheet.error.ErrorBottomSheetConfig

data class HomeScreenState(
    val errorBottomSheetConfig: ErrorBottomSheetConfig? = null,
    val showLoading: Boolean = false,
    val timing: Timing? = null,
    val isReviewShown: Boolean = false,
    val currentPrayer: ActivePrayer? = null,
)

data class ActivePrayer(
    val nameResId: Int,
    val startTimeRaw: String,
    val endInMinutes: Int,
    val progress: Float,
)
