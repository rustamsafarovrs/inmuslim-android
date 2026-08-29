package tj.rsdevteam.inmuslim.feature.tasbih.ui.entryhistory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tj.rsdevteam.inmuslim.core.TitleValue
import tj.rsdevteam.inmuslim.core.asTextRes
import tj.rsdevteam.inmuslim.core.resolve
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.core.utils.NumberFormatter
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord
import tj.rsdevteam.inmuslim.feature.tasbih.ui.common.CountBadge
import tj.rsdevteam.inmuslim.feature.tasbih.ui.common.HistoryEmptyState
import tj.rsdevteam.inmuslim.feature.tasbih.ui.common.HistorySummaryCard
import tj.rsdevteam.inmuslim.res.R
import tj.rstech.uicomponents.ProgressIndicator

@Composable
fun TasbihEntryHistoryScreen() {
    val router = LocalRouter.current
    val viewModel: TasbihEntryHistoryViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    TasbihEntryHistoryScreen(
        state = state,
        didClickBack = { router.navigateUp() },
    )
}

@Composable
private fun TasbihEntryHistoryScreen(
    state: TasbihEntryHistoryScreenState,
    didClickBack: () -> Unit = {},
) {
    Scaffold(
        topBar = { HistoryTopBar(tasbihName = state.tasbihName, didClickBack = didClickBack) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { safeArea ->
        val contentModifier = Modifier.padding(safeArea)
        when {
            state.records.isNotEmpty() -> LazyColumn(modifier = contentModifier.fillMaxSize()) {
                item {
                    HistorySummaryCard(
                        stats = summaryStats(state),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
                items(items = state.records, key = { it.id }) { record ->
                    HistoryItem(record = record)
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    )
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopBar(
    tasbihName: String,
    didClickBack: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(R.string.tasbih_title_history),
                    style = InmuslimTypo.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                )
                if (tasbihName.isNotEmpty()) {
                    Text(
                        text = tasbihName,
                        style = InmuslimTypo.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = didClickBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
private fun summaryStats(state: TasbihEntryHistoryScreenState): List<TitleValue> = listOf(
    TitleValue(title = stringResource(R.string.tasbih_other_total), value = NumberFormatter.format(state.totalCount)),
    TitleValue(title = stringResource(R.string.tasbih_other_days), value = NumberFormatter.format(state.activeDays)),
    TitleValue(title = stringResource(R.string.tasbih_other_best), value = NumberFormatter.format(state.bestCount)),
)

@Composable
private fun HistoryItem(record: TasbihRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = record.dateLabel.resolve(),
            modifier = Modifier.weight(1f),
            style = InmuslimTypo.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
        )
        CountBadge(count = record.count)
    }
}

@Suppress("MagicNumber")
@Preview(showBackground = true)
@Composable
private fun TasbihEntryHistoryScreenPreview() {
    InmuslimTheme {
        TasbihEntryHistoryScreen(
            state = TasbihEntryHistoryScreenState(
                tasbihName = "SubhanAllah",
                records = listOf(
                    TasbihRecord(1, 1, 99, "2026-04-27", "Today".asTextRes()),
                    TasbihRecord(2, 1, 33, "2026-04-26", "Yesterday".asTextRes()),
                    TasbihRecord(3, 1, 100, "2026-04-25", "25 April 2026".asTextRes()),
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TasbihEntryHistoryEmptyPreview() {
    InmuslimTheme {
        TasbihEntryHistoryScreen(
            state = TasbihEntryHistoryScreenState(tasbihName = "SubhanAllah"),
        )
    }
}
