package tj.rsdevteam.inmuslim.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.res.R
import tj.rsdevteam.inmuslim.utils.findActivity
import tj.rsdevteam.inmuslim.utils.is24HourFormat
import tj.rsdevteam.inmuslim.utils.launchInAppReview
import tj.rstech.uicomponents.bottomsheet.error.ErrorBottomSheet

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current
    val router = LocalRouter.current

    val shouldRequestReview = viewModel.state.shouldRequestReview
    LaunchedEffect(shouldRequestReview) {
        if (shouldRequestReview) {
            context.findActivity().launchInAppReview { viewModel.handleEvent(HomeUIEvent.ReviewShowed) }
        }
    }

    LifecycleStartEffect(viewModel) {
        viewModel.handleEvent(HomeUIEvent.DidStart)
        onStopOrDispose { viewModel.handleEvent(HomeUIEvent.DidStop) }
    }

    HomeScreen(
        state = viewModel.state,
        didClickSettings = { router.navigate(Screen.Settings) },
        didClickTasbih = { router.navigate(Screen.TasbihList) },
        handleEvent = { viewModel.handleEvent(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeScreenState,
    didClickSettings: () -> Unit = {},
    didClickTasbih: () -> Unit = {},
    handleEvent: (HomeUIEvent) -> Unit = {},
) {
    val errorState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HomeTopBar(scrollBehavior, didClickTasbih, didClickSettings) },
    ) { paddingValues ->
        HomeContent(state = state, modifier = Modifier.padding(paddingValues))
    }

    state.errorBottomSheetConfig?.let {
        ErrorBottomSheet(
            sheetState = errorState,
            errorBottomSheetConfig = it,
            dismiss = { handleEvent(HomeUIEvent.DismissDialog) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    didClickTasbih: () -> Unit,
    didClickSettings: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = didClickTasbih) {
                Icon(
                    painter = painterResource(R.drawable.ic_tasbih_24),
                    contentDescription = stringResource(R.string.tasbih_title_tasbih),
                )
            }
            IconButton(onClick = didClickSettings) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings_24),
                    contentDescription = stringResource(R.string.common_title_settings),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun HomeContent(state: HomeScreenState, modifier: Modifier = Modifier) {
    val is24Hour = LocalContext.current.is24HourFormat()
    val timing = state.timing
    val hero = state.hero
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        if (timing != null && hero != null) {
            PrayerHeroCard(hero = hero, is24Hour = is24Hour, modifier = Modifier.padding(top = 4.dp))
            HomeSchedule(
                timing = timing,
                statuses = state.statuses,
                is24Hour = is24Hour,
                modifier = Modifier.padding(top = 14.dp),
            )
        } else if (state.showLoading) {
            HomeSkeleton(modifier = Modifier.padding(top = 4.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview
@Composable
private fun HomeScreenActivePreview() {
    val timing = Timing(
        fajr = "05:00",
        sunrise = "06:30",
        zuhr = "12:30",
        asr = "16:00",
        sunset = "18:30",
        maghrib = "18:45",
        isha = "20:00",
    )
    val hero = PrayerSchedule.heroAt(timing = timing, nowInSeconds = 17 * 3600)
    InmuslimTheme {
        HomeScreen(
            state = HomeScreenState(
                timing = timing,
                hero = hero,
                statuses = PrayerSchedule.statusesAt(timing = timing, hero = hero, nowInSeconds = 17 * 3600),
            ),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenActiveDarkPreview() {
    val timing = Timing(
        fajr = "05:00",
        sunrise = "06:30",
        zuhr = "12:30",
        asr = "16:00",
        sunset = "18:30",
        maghrib = "18:45",
        isha = "20:00",
    )
    val hero = PrayerSchedule.heroAt(timing = timing, nowInSeconds = 17 * 3600)
    InmuslimTheme {
        HomeScreen(
            state = HomeScreenState(
                timing = timing,
                hero = hero,
                statuses = PrayerSchedule.statusesAt(timing = timing, hero = hero, nowInSeconds = 17 * 3600),
            ),
        )
    }
}

@Preview
@Composable
private fun HomeScreenBetweenPrayersPreview() {
    val timing = Timing(
        fajr = "05:00",
        sunrise = "06:30",
        zuhr = "12:30",
        asr = "16:00",
        sunset = "18:30",
        maghrib = "18:45",
        isha = "20:00",
    )
    val hero = PrayerSchedule.heroAt(timing = timing, nowInSeconds = 9 * 3600)
    InmuslimTheme {
        HomeScreen(
            state = HomeScreenState(
                timing = timing,
                hero = hero,
                statuses = PrayerSchedule.statusesAt(timing = timing, hero = hero, nowInSeconds = 9 * 3600),
            ),
        )
    }
}

@Preview
@Composable
private fun HomeScreenLoadingPreview() {
    InmuslimTheme {
        HomeScreen(state = HomeScreenState(showLoading = true))
    }
}
