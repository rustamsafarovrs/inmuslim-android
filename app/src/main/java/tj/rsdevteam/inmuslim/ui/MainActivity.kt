package tj.rsdevteam.inmuslim.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import tj.rsdevteam.inmuslim.core.Const
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.Router
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.ui.home.HomeScreen
import tj.rsdevteam.inmuslim.ui.region.RegionScreen
import tj.rsdevteam.inmuslim.ui.settings.SettingsScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination = getStartDestination()

        setContent {
            InmuslimTheme {
                val navController = rememberNavController()
                val router = remember(navController) { Router(navController) }

                CompositionLocalProvider(LocalRouter provides router) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Navigation(startDestination)
                    }
                }
            }
        }
    }
}

@Composable
fun Navigation(startDestination: Screen) {
    val router = LocalRouter.current
    NavHost(navController = router.controller, startDestination = startDestination) {
        composable<Screen.Regions> {
            RegionScreen()
        }
        composable<Screen.Main> {
            HomeScreen()
        }
        composable<Screen.Settings> {
            SettingsScreen()
        }
    }
}

private fun MainActivity.getStartDestination(): Screen {
    return if (intent.getBooleanExtra(Const.OPEN_ONBOARDING, false)) {
        Screen.Regions
    } else {
        Screen.Main
    }
}
