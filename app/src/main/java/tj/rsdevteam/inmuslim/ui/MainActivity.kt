package tj.rsdevteam.inmuslim.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import tj.rsdevteam.inmuslim.core.Const
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.Router
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.feature.tasbih.ui.calculator.TasbihScreen
import tj.rsdevteam.inmuslim.feature.tasbih.ui.entryhistory.TasbihEntryHistoryScreen
import tj.rsdevteam.inmuslim.feature.tasbih.ui.history.TasbihHistoryScreen
import tj.rsdevteam.inmuslim.feature.tasbih.ui.list.TasbihListScreen
import tj.rsdevteam.inmuslim.res.R
import tj.rsdevteam.inmuslim.ui.home.HomeScreen
import tj.rsdevteam.inmuslim.ui.region.RegionScreen
import tj.rsdevteam.inmuslim.ui.settings.SettingsScreen
import tj.rsdevteam.inmuslim.utils.InAppUpdateManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var inAppUpdateManager: InAppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination = getStartDestination()

        inAppUpdateManager = InAppUpdateManager(this)
        if (savedInstanceState == null) {
            inAppUpdateManager.checkForUpdate()
        }

        setContent {
            InmuslimTheme {
                val navController = rememberNavController()
                val router = remember(navController) { Router(navController) }

                CompositionLocalProvider(LocalRouter provides router) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Navigation(startDestination)
                            UpdateDownloadedSnackbar(
                                isUpdateDownloaded = inAppUpdateManager.isUpdateDownloaded,
                                completeUpdate = { inAppUpdateManager.completeUpdate() },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .navigationBarsPadding(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateDownloadedSnackbar(
    isUpdateDownloaded: Boolean,
    completeUpdate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val message = stringResource(id = R.string.base_description_update_downloaded)
    val actionLabel = stringResource(id = R.string.base_action_update_install)

    LaunchedEffect(key1 = isUpdateDownloaded) {
        if (isUpdateDownloaded) {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Indefinite,
            )
            if (result == SnackbarResult.ActionPerformed) {
                completeUpdate()
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState, modifier = modifier)
}

@Composable
fun Navigation(startDestination: Screen) {
    val router = LocalRouter.current
    NavHost(navController = router.controller, startDestination = startDestination) {
        composable<Screen.Regions> { RegionScreen() }
        composable<Screen.Main> { HomeScreen() }
        composable<Screen.Settings> { SettingsScreen() }
        composable<Screen.TasbihList> { TasbihListScreen() }
        composable<Screen.TasbihCalculator> { TasbihScreen() }
        composable<Screen.TasbihEntryHistory> { TasbihEntryHistoryScreen() }
        composable<Screen.TasbihHistory> { TasbihHistoryScreen() }
    }
}

private fun MainActivity.getStartDestination(): Screen {
    return if (intent.getBooleanExtra(Const.OPEN_ONBOARDING, false)) {
        Screen.Regions
    } else {
        Screen.Main
    }
}
