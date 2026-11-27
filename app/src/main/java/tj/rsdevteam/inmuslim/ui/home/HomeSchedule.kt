package tj.rsdevteam.inmuslim.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.rsdevteam.inmuslim.data.models.Timing
import tj.rsdevteam.inmuslim.res.R
import tj.rsdevteam.inmuslim.utils.TimeUtils

private val PrayerGroups = listOf(
    listOf(Prayer.FAJR),
    listOf(Prayer.ZUHR, Prayer.ASR),
    listOf(Prayer.MAGHRIB, Prayer.ISHA),
)
private val SunEvents = listOf(Prayer.SUNRISE, Prayer.SUNSET)

@Composable
fun HomeSchedule(
    timing: Timing,
    statuses: Map<Prayer, PrayerStatus>,
    is24Hour: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        PrayerGroups.forEachIndexed { index, group ->
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                group.forEachIndexed { i, prayer ->
                    PrayerRow(
                        prayer = prayer,
                        time = TimeUtils.formatTime(timing.timeOf(prayer), is24Hour),
                        status = statuses[prayer] ?: PrayerStatus.FUTURE,
                        shape = segmentShape(i, group.size),
                    )
                }
            }
            SunEvents.getOrNull(index)?.let { SunEvent(it, TimeUtils.formatTime(timing.timeOf(it), is24Hour)) }
        }
    }
}

@Composable
private fun SunEvent(prayer: Prayer, time: String) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        PrayerIcon(prayer, tint = color)
        Text(
            text = "${stringResource(prayer.nameResId)} · $time",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontFeatureSettings = TABULAR_NUMBERS,
            ),
            color = color,
        )
    }
}

@Composable
private fun PrayerRow(prayer: Prayer, time: String, status: PrayerStatus, shape: Shape) {
    val scheme = MaterialTheme.colorScheme
    val highlight = highlightColors()
    val isNow = status == PrayerStatus.NOW
    val isPast = status == PrayerStatus.PAST
    val container = if (isNow) highlight.container else scheme.surfaceContainerLow
    val content = when {
        isNow -> highlight.content
        isPast -> scheme.onSurfaceVariant
        else -> scheme.onSurface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (isNow) 72.dp else 64.dp)
            .background(container, shape)
            .then(if (status == PrayerStatus.NEXT) Modifier.border(2.dp, scheme.primary, shape) else Modifier)
            .padding(start = 8.dp, top = 4.dp, end = 20.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RowLead(prayer, status, container, content)
        RowTitle(prayer, status, content, container, modifier = Modifier.weight(1f))
        Text(
            text = time,
            style = MaterialTheme.typography.headlineSmall.copy(
                lineHeight = 28.sp,
                fontWeight = if (isPast) FontWeight.Normal else FontWeight.SemiBold,
                letterSpacing = (-0.3).sp,
                fontFeatureSettings = TABULAR_NUMBERS,
            ),
            color = if (status == PrayerStatus.NEXT) scheme.primary else content,
            maxLines = 1,
        )
    }
}

@Composable
private fun RowLead(prayer: Prayer, status: PrayerStatus, container: Color, content: Color) {
    val scheme = MaterialTheme.colorScheme
    when (status) {
        PrayerStatus.NOW -> StarBadge(color = content, filled = true) { PrayerIcon(prayer, tint = container) }
        PrayerStatus.NEXT -> StarBadge(color = scheme.primary, filled = false) {
            PrayerIcon(prayer, tint = scheme.primary)
        }
        else -> Box(
            modifier = Modifier
                .size(48.dp)
                .background(scheme.surfaceContainerHigh, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            PrayerIcon(prayer, tint = scheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun RowTitle(
    prayer: Prayer,
    status: PrayerStatus,
    content: Color,
    container: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(prayer.nameResId),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    lineHeight = 22.sp,
                    letterSpacing = 0.sp,
                    fontWeight = if (status == PrayerStatus.PAST) FontWeight.Normal else FontWeight.Medium,
                ),
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            when (status) {
                PrayerStatus.NOW -> StatusTag(R.string.base_other_tag_now, color = content, solidContent = container)
                PrayerStatus.NEXT -> StatusTag(R.string.base_other_tag_next, color = MaterialTheme.colorScheme.primary)
                else -> Unit
            }
        }
        Text(
            text = stringResource(prayer.arabicResId),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = ArabicFontFamily,
                fontSize = 15.sp,
                lineHeight = 20.sp,
            ),
            color = if (status == PrayerStatus.NOW) content else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun StatusTag(textResId: Int, color: Color, solidContent: Color? = null) {
    val shape = RoundedCornerShape(10.dp)
    val background = if (solidContent != null) {
        Modifier.background(color, shape)
    } else {
        Modifier.border(1.5.dp, color, shape)
    }
    Box(
        modifier = Modifier
            .height(20.dp)
            .then(background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(textResId),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp),
            color = solidContent ?: color,
            maxLines = 1,
        )
    }
}
