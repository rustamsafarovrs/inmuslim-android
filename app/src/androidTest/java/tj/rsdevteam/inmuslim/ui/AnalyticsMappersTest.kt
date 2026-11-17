package tj.rsdevteam.inmuslim.ui

import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import tj.rsdevteam.inmuslim.analytics.AnalyticsScreen
import tj.rsdevteam.inmuslim.core.router.Screen

@RunWith(AndroidJUnit4::class)
class AnalyticsMappersTest {

    @Test
    fun mapsEveryDestinationToItsOwnScreen() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val controller = NavHostController(context).apply { navigatorProvider.addNavigator(ComposeNavigator()) }
        val graph = controller.createGraph(startDestination = Screen.Main) {
            composable<Screen.Main> {}
            composable<Screen.Regions> {}
            composable<Screen.Settings> {}
            composable<Screen.TasbihList> {}
            composable<Screen.TasbihCalculator> {}
            composable<Screen.TasbihHistory> {}
            composable<Screen.TasbihEntryHistory> {}
        }

        val screens = listOf(
            graph.findNode<Screen.Main>(),
            graph.findNode<Screen.Regions>(),
            graph.findNode<Screen.Settings>(),
            graph.findNode<Screen.TasbihList>(),
            graph.findNode<Screen.TasbihCalculator>(),
            graph.findNode<Screen.TasbihHistory>(),
            graph.findNode<Screen.TasbihEntryHistory>(),
        ).map { it?.toAnalyticsScreen() }

        assertEquals(
            listOf(
                AnalyticsScreen.HOME,
                AnalyticsScreen.REGIONS,
                AnalyticsScreen.SETTINGS,
                AnalyticsScreen.TASBIH_LIST,
                AnalyticsScreen.TASBIH_CALCULATOR,
                AnalyticsScreen.TASBIH_HISTORY,
                AnalyticsScreen.TASBIH_ENTRY_HISTORY,
            ),
            screens,
        )
        assertEquals(AnalyticsScreen.entries.toSet(), screens.toSet())
    }

    @Test
    fun ignoresTheGraphItself() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val controller = NavHostController(context).apply { navigatorProvider.addNavigator(ComposeNavigator()) }
        val graph = controller.createGraph(startDestination = Screen.Main) { composable<Screen.Main> {} }

        assertEquals(null, graph.toAnalyticsScreen())
    }
}
