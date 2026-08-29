package tj.rsdevteam.inmuslim.feature.tasbih.ui.common

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tj.rsdevteam.inmuslim.core.TitleValue
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimShapes
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTheme
import tj.rsdevteam.inmuslim.core.router.theme.InmuslimTypo
import tj.rsdevteam.inmuslim.core.utils.NumberFormatter
import tj.rsdevteam.inmuslim.res.R

@Composable
internal fun HistorySummaryCard(
    stats: List<TitleValue>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = InmuslimShapes.large,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            stats.forEachIndexed { index, stat ->
                if (index > 0) {
                    VerticalDivider(
                        modifier = Modifier.height(28.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                    )
                }
                StatColumn(stat = stat, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatColumn(stat: TitleValue, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stat.value,
            style = InmuslimTypo.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stat.title,
            style = InmuslimTypo.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Surface(
        modifier = modifier,
        shape = InmuslimShapes.small,
        color = containerColor,
    ) {
        Text(
            text = NumberFormatter.format(count),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = InmuslimTypo.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = contentColor,
        )
    }
}

@Composable
internal fun HistoryEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_history_24),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = InmuslimTypo.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = InmuslimTypo.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistorySummaryCardPreview() {
    InmuslimTheme {
        HistorySummaryCard(
            stats = listOf(
                TitleValue(title = "Total", value = "1 232"),
                TitleValue(title = "Days", value = "14"),
                TitleValue(title = "Average", value = "88"),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Suppress("MagicNumber")
@Preview(showBackground = true)
@Composable
private fun CountBadgePreview() {
    InmuslimTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CountBadge(count = 33)
            CountBadge(count = 1000)
            CountBadge(
                count = 99,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 320)
@Composable
private fun HistoryEmptyStatePreview() {
    InmuslimTheme {
        HistoryEmptyState(
            title = "No records yet",
            description = "Start a tasbih and your daily counts will show up here.",
        )
    }
}
