package tj.rsdevteam.inmuslim.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimShapes
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.res.R
import tj.rsdevteam.inmuslim.ui.common.ErrorBottomSheet
import tj.rsdevteam.inmuslim.ui.common.ProgressIndicator
import tj.rsdevteam.inmuslim.utils.TimeUtils
import tj.rsdevteam.inmuslim.utils.findActivity
import tj.rsdevteam.inmuslim.utils.is24HourFormat
import tj.rsdevteam.inmuslim.utils.launchInAppReview

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current
    val router = LocalRouter.current

    LaunchedEffect(key1 = Unit) {
        if (!viewModel.state.isReviewShown) {
            context.findActivity().launchInAppReview { viewModel.handleEvent(HomeUIEvent.ReviewShowed) }
        }
    }

    HomeScreen(
        state = viewModel.state,
        didClickSettings = { router.navigate(Screen.Settings) },
        handleEvent = { viewModel.handleEvent(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeScreenState,
    didClickSettings: () -> Unit,
    handleEvent: (HomeUIEvent) -> Unit
) {
    val errorState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = didClickSettings) {
                        Icon(painterResource(R.drawable.ic_settings_24), contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        val is24Hour = LocalContext.current.is24HourFormat()
        if (state.showLoading) {
            ProgressIndicator()
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            if (state.timing != null) {
                Spacer(modifier = Modifier.height(20.dp))
                state.currentPrayer?.let {
                    CurrentPrayerCard(it, is24Hour)
                    Spacer(modifier = Modifier.height(20.dp))
                }
                TimeItems(state.timing, state.currentPrayer?.nameResId, is24Hour)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (state.errorBottomSheetConfig != null) {
        ErrorBottomSheet(
            sheetState = errorState,
            errorBottomSheetConfig = state.errorBottomSheetConfig!!,
            dismiss = { handleEvent(HomeUIEvent.DismissDialog) }
        )
    }
}

@Composable
private fun CurrentPrayerCard(activePrayer: ActivePrayer, is24Hour: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = InmuslimShapes.large)
            .background(MaterialTheme.colorScheme.primary)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.current_prayer_time),
            style = InmuslimTypo.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = activePrayer.nameResId),
            style = InmuslimTypo.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { activePrayer.progress },
                modifier = Modifier.size(120.dp),
                strokeWidth = 8.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = TimeUtils.formatTime(activePrayer.startTimeRaw, is24Hour),
                    style = InmuslimTypo.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = TimeUtils.formatMinutes(activePrayer.endInMinutes, is24Hour),
                    style = InmuslimTypo.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun TimeItems(timing: Timing, currentPrayerResId: Int?, is24Hour: Boolean) {
    Column {
        TimeItem(
            title = stringResource(R.string.fajr),
            start = TimeUtils.formatTime(timing.fajr, is24Hour),
            isSelected = currentPrayerResId == R.string.fajr
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.sunrise),
            start = TimeUtils.formatTime(timing.sunrise, is24Hour),
            isSelected = false
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.zuhr),
            start = TimeUtils.formatTime(timing.zuhr, is24Hour),
            isSelected = currentPrayerResId == R.string.zuhr
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.asr),
            start = TimeUtils.formatTime(timing.asr, is24Hour),
            isSelected = currentPrayerResId == R.string.asr
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.sunset),
            start = TimeUtils.formatTime(timing.sunset, is24Hour),
            isSelected = false
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.maghrib),
            start = TimeUtils.formatTime(timing.maghrib, is24Hour),
            isSelected = currentPrayerResId == R.string.maghrib
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.isha),
            start = TimeUtils.formatTime(timing.isha, is24Hour),
            isSelected = currentPrayerResId == R.string.isha
        )
    }
}

@Composable
private fun TimeItem(title: String, start: String, isSelected: Boolean = false) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .defaultMinSize(minHeight = 60.dp)
            .clip(shape = InmuslimShapes.large)
            .background(backgroundColor)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = InmuslimTypo.titleMedium,
            color = contentColor
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = start,
            style = InmuslimTypo.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor
        )
    }
}

@Preview
@Composable
private fun HomeScreenFajrPreview() {
    InmuslimTheme {
        HomeScreen(
            state = HomeScreenState(
                timing = Timing(
                    fajr = "05:00",
                    sunrise = "06:30",
                    zuhr = "12:30",
                    asr = "16:00",
                    sunset = "18:30",
                    maghrib = "18:45",
                    isha = "20:00"
                ),
                currentPrayer = ActivePrayer(
                    nameResId = R.string.fajr,
                    startTimeRaw = "05:00",
                    endInMinutes = 6 * 60 + 30,
                    progress = 0.3f
                )
            ),
            didClickSettings = {},
            handleEvent = {}
        )
    }
}

@Preview
@Composable
private fun HomeScreenAsrPreview() {
    InmuslimTheme {
        HomeScreen(
            state = HomeScreenState(
                timing = Timing(
                    fajr = "05:00",
                    sunrise = "06:30",
                    zuhr = "12:30",
                    asr = "16:00",
                    sunset = "18:30",
                    maghrib = "18:45",
                    isha = "20:00"
                ),
                currentPrayer = ActivePrayer(
                    nameResId = R.string.asr,
                    startTimeRaw = "16:00",
                    endInMinutes = 18 * 60 + 30,
                    progress = 0.6f
                )
            ),
            didClickSettings = {},
            handleEvent = {}
        )
    }
}

@Preview
@Composable
private fun HomeScreenMaghribPreview() {
    InmuslimTheme {
        HomeScreen(
            state = HomeScreenState(
                timing = Timing(
                    fajr = "05:00",
                    sunrise = "06:30",
                    zuhr = "12:30",
                    asr = "16:00",
                    sunset = "18:30",
                    maghrib = "18:45",
                    isha = "20:00"
                ),
                currentPrayer = ActivePrayer(
                    nameResId = R.string.maghrib,
                    startTimeRaw = "18:45",
                    endInMinutes = 20 * 60,
                    progress = 0.4f
                )
            ),
            didClickSettings = {},
            handleEvent = {}
        )
    }
}

@Preview
@Composable
private fun HomeScreenNoPrayerPreview() {
    InmuslimTheme {
        HomeScreen(
            state = HomeScreenState(
                timing = Timing(
                    fajr = "05:00",
                    sunrise = "06:30",
                    zuhr = "12:30",
                    asr = "16:00",
                    sunset = "18:30",
                    maghrib = "18:45",
                    isha = "20:00"
                ),
                currentPrayer = null
            ),
            didClickSettings = {},
            handleEvent = {}
        )
    }
}

@Preview
@Composable
private fun TimeItemPreview() {
    InmuslimTheme {
        TimeItem(title = "title", start = "00:00:00")
    }
}
