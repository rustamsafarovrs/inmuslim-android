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
import tj.rsdevteam.inmuslim.utils.findActivity
import tj.rsdevteam.inmuslim.utils.launchInAppReview

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@Composable
fun TimeItem(title: String, start: String, isSelected: Boolean = false) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val router = LocalRouter.current
    val errorState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(key1 = Unit) {
        if (!viewModel.state.isReviewShown) {
            context.findActivity().launchInAppReview { viewModel.handleEvent(HomeUIEvent.ReviewShowed) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { router.navigate(Screen.Settings) }) {
                        Icon(painterResource(R.drawable.ic_settings_24), contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        if (viewModel.state.showLoading) {
            ProgressIndicator()
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            if (viewModel.state.timing != null) {
                val timing = viewModel.state.timing!!
                Spacer(modifier = Modifier.height(20.dp))
                viewModel.state.currentPrayer?.let {
                    CurrentPrayerCard(it)
                    Spacer(modifier = Modifier.height(20.dp))
                }
                TimeItems(timing, viewModel.state.currentPrayer?.nameResId)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (viewModel.state.errorBottomSheetConfig != null) {
        ErrorBottomSheet(
            sheetState = errorState,
            errorBottomSheetConfig = viewModel.state.errorBottomSheetConfig!!,
            dismiss = { viewModel.handleEvent(HomeUIEvent.DismissDialog) }
        )
    }
}

@Composable
fun CurrentPrayerCard(activePrayer: ActivePrayer) {
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
                    text = activePrayer.startTime,
                    style = InmuslimTypo.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = activePrayer.endTime,
                    style = InmuslimTypo.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun TimeItems(timing: Timing, currentPrayerResId: Int?) {
    Column {
        TimeItem(
            title = stringResource(R.string.fajr),
            start = timing.fajr,
            isSelected = currentPrayerResId == R.string.fajr
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.sunrise),
            start = timing.sunrise,
            isSelected = false // Sunrise is not a prayer itself, but a boundary
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.zuhr),
            start = timing.zuhr,
            isSelected = currentPrayerResId == R.string.zuhr
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.asr),
            start = timing.asr,
            isSelected = currentPrayerResId == R.string.asr
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.sunset),
            start = timing.sunset,
            isSelected = false
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.maghrib),
            start = timing.maghrib,
            isSelected = currentPrayerResId == R.string.maghrib
        )
        Spacer(modifier = Modifier.height(12.dp))
        TimeItem(
            title = stringResource(R.string.isha),
            start = timing.isha,
            isSelected = currentPrayerResId == R.string.isha
        )
    }
}

@Preview
@Composable
fun TimeItemPreview() {
    InmuslimTheme {
        TimeItem(title = "title", start = "00:00:00")
    }
}
