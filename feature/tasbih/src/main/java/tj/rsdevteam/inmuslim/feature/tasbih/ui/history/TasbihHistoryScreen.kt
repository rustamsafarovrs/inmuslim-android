package tj.rsdevteam.inmuslim.feature.tasbih.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import tj.rsdevteam.inmuslim.core.TitleValue
import tj.rsdevteam.inmuslim.core.asTextRes
import tj.rsdevteam.inmuslim.core.resolve
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.Screen
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimShapes
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.core.utils.NumberFormatter
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihDayHistory
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihHistoryEntry
import tj.rsdevteam.inmuslim.feature.tasbih.ui.common.CountBadge
import tj.rsdevteam.inmuslim.feature.tasbih.ui.common.HistoryEmptyState
import tj.rsdevteam.inmuslim.feature.tasbih.ui.common.HistorySummaryCard
import tj.rsdevteam.inmuslim.res.R
import tj.rstech.uicomponents.ProgressIndicator
import tj.rstech.uicomponents.appbar.LargeTopAppBar

@Composable
fun TasbihHistoryScreen() {
    val router = LocalRouter.current
    val viewModel: TasbihHistoryViewModel = hiltViewModel()
    TasbihHistoryScreen(
        state = viewModel.state,
        didClickBack = { router.navigateUp() },
        didSelectEntry = { router.navigate(Screen.TasbihEntryHistory(it.tasbihId)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasbihHistoryScreen(
    state: TasbihHistoryScreenState,
    didClickBack: () -> Unit = {},
    didSelectEntry: (TasbihHistoryEntry) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(stringResource(R.string.tasbih_title_all_history), scrollBehavior, didClickBack)
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { safeArea ->
        val contentModifier = Modifier.padding(safeArea)
        when {
            state.days.isNotEmpty() -> LazyColumn(modifier = contentModifier.fillMaxSize()) {
                item {
                    HistorySummaryCard(
                        stats = summaryStats(state),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }
                days(days = state.days, didSelectEntry = didSelectEntry)
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }

            state.base.isLoading -> Box(modifier = contentModifier) { ProgressIndicator() }

            else -> HistoryEmptyState(
                title = stringResource(R.string.tasbih_other_no_history),
                description = stringResource(R.string.tasbih_description_no_history),
                modifier = contentModifier,
            )
        }
    }
}

private fun LazyListScope.days(
    days: List<TasbihDayHistory>,
    didSelectEntry: (TasbihHistoryEntry) -> Unit,
) {
    days.forEach { day ->
        stickyHeader(key = day.date) { DayHeader(day = day) }
        items(items = day.entries, key = { "${day.date}_${it.tasbihId}" }) { entry ->
            EntryItem(
                entry = entry,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                didClick = { didSelectEntry(entry) },
            )
        }
    }
}

@Composable
private fun summaryStats(state: TasbihHistoryScreenState): List<TitleValue> = listOf(
    TitleValue(title = stringResource(R.string.tasbih_other_total), value = NumberFormatter.format(state.totalCount)),
    TitleValue(title = stringResource(R.string.tasbih_other_days), value = NumberFormatter.format(state.activeDays)),
    TitleValue(title = stringResource(R.string.tasbih_title_tasbih), value = NumberFormatter.format(state.tasbihCount)),
)

@Composable
private fun DayHeader(day: TasbihDayHistory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = day.dateLabel.resolve(),
            modifier = Modifier.weight(1f),
            style = InmuslimTypo.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
        )
        CountBadge(
            count = day.total,
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            contentColor = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun EntryItem(
    entry: TasbihHistoryEntry,
    modifier: Modifier = Modifier,
    didClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(InmuslimShapes.large)
            .clickable { didClick() },
        shape = InmuslimShapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = entry.tasbihName,
                modifier = Modifier.weight(1f),
                style = InmuslimTypo.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(12.dp))
            CountBadge(count = entry.count)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.outline_arrow_forward_ios_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Suppress("MagicNumber")
@Preview(showBackground = true)
@Composable
private fun TasbihHistoryScreenPreview() {
    InmuslimTheme {
        TasbihHistoryScreen(
            state = TasbihHistoryScreenState(
                days = listOf(
                    TasbihDayHistory(
                        date = "2026-04-27",
                        dateLabel = "Today".asTextRes(),
                        total = 132,
                        entries = listOf(
                            TasbihHistoryEntry(1, "SubhanAllah", 99, "2026-04-27", "Today".asTextRes()),
                            TasbihHistoryEntry(2, "Alhamdulillah", 33, "2026-04-27", "Today".asTextRes()),
                        ),
                    ),
                    TasbihDayHistory(
                        date = "2026-04-25",
                        dateLabel = "25 April 2026".asTextRes(),
                        total = 100,
                        entries = listOf(
                            TasbihHistoryEntry(3, "Allahu Akbar", 100, "2026-04-25", "25 April 2026".asTextRes()),
                        ),
                    ),
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TasbihHistoryEmptyPreview() {
    InmuslimTheme {
        TasbihHistoryScreen(state = TasbihHistoryScreenState())
    }
}
