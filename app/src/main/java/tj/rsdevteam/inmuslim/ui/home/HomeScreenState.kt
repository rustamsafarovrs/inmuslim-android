package tj.rsdevteam.inmuslim.ui.home

import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rstech.uicomponents.bottomsheet.error.ErrorBottomSheetConfig

data class HomeScreenState(
    val errorBottomSheetConfig: ErrorBottomSheetConfig? = null,
    val showLoading: Boolean = false,
    val timing: Timing? = null,
    val isReviewShown: Boolean = false,
    val hero: PrayerHero? = null,
    val statuses: Map<Prayer, PrayerStatus> = emptyMap(),
)
