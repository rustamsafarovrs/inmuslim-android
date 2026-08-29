package tj.rsdevteam.inmuslim.ui

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import tj.rsdevteam.inmuslim.analytics.AnalyticsScreen
import tj.rsdevteam.inmuslim.core.router.Screen

/**
 * Created by Rustam Safarov on 8/29/26.
 * github.com/rustamsafarovrs
 */

internal fun NavDestination.toAnalyticsScreen(): AnalyticsScreen? = when {
    hasRoute<Screen.Main>() -> AnalyticsScreen.HOME
    hasRoute<Screen.Regions>() -> AnalyticsScreen.REGIONS
    hasRoute<Screen.Settings>() -> AnalyticsScreen.SETTINGS
    hasRoute<Screen.TasbihList>() -> AnalyticsScreen.TASBIH_LIST
    hasRoute<Screen.TasbihCalculator>() -> AnalyticsScreen.TASBIH_CALCULATOR
    hasRoute<Screen.TasbihHistory>() -> AnalyticsScreen.TASBIH_HISTORY
    hasRoute<Screen.TasbihEntryHistory>() -> AnalyticsScreen.TASBIH_ENTRY_HISTORY
    else -> null
}
