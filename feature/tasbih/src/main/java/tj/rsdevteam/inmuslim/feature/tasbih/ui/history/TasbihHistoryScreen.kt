package tj.rsdevteam.inmuslim.feature.tasbih.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tj.rsdevteam.inmuslim.core.router.LocalRouter
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.core.utils.DateUtils
import tj.rsdevteam.inmuslim.feature.tasbih.data.models.TasbihRecord
import tj.rsdevteam.inmuslim.res.R

@Composable
fun TasbihHistoryScreen() {
    val router = LocalRouter.current
    val viewModel: TasbihHistoryViewModel = hiltViewModel()
    TasbihHistoryScreen(
        state = viewModel.state,
        didClickBack = { router.navigateUp() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasbihHistoryScreen(
    state: TasbihHistoryScreenState,
    didClickBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.tasbih_title_history),
                            style = InmuslimTypo.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        )
                        if (state.tasbihName.isNotEmpty()) {
                            Text(
                                text = state.tasbihName,
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
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (state.records.isEmpty()) {
            HistoryEmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                items(state.records) { record ->
                    HistoryItem(record = record)
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    )
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun HistoryItem(record: TasbihRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = DateUtils.formatDateTime(
                    record.date,
                    resultPattern = DateUtils.HUMAN_DATE,
                    dateTimePattern = DateUtils.ISO_DATE,
                ) ?: "",
                style = InmuslimTypo.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            text = record.count.toString(),
            style = InmuslimTypo.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun HistoryEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.ic_history_24),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.tasbih_other_no_history),
                style = InmuslimTypo.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                tasbihName = "SubhanAllah",
                records = listOf(
                    TasbihRecord(1, 1, 99, "2026-04-27"),
                    TasbihRecord(2, 1, 33, "2026-04-26"),
                    TasbihRecord(3, 1, 100, "2026-04-25"),
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TasbihHistoryEmptyPreview() {
    InmuslimTheme {
        TasbihHistoryScreen(
            state = TasbihHistoryScreenState(tasbihName = "SubhanAllah"),
        )
    }
}
