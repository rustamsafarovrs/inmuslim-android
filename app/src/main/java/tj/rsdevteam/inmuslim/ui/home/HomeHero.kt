package tj.rsdevteam.inmuslim.ui.home

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.rsdevteam.inmuslim.res.R
import tj.rsdevteam.inmuslim.utils.TimeUtils

private const val PATTERN_ALPHA = 0.2f
private const val PATTERN_DEPTH = 0.62f
private const val FRAME_ALPHA_ACTIVE = 0.28f
private const val FRAME_ALPHA_IDLE = 0.6f
private const val PULSE_ALPHA = 0.6f
private const val PULSE_SCALE = 2.8f
private const val PULSE_MILLIS = 1600

private val HeroMinHeight = 344.dp
private val FrameInset = 10.dp
private val FrameStroke = 1.5.dp
private val FrameDashOn = 5.dp
private val FrameDashOff = 6.dp
private val OutlineWidth = 1.5.dp

@Immutable
private data class HeroColors(
    val container: Color,
    val content: Color,
    val secondaryContent: Color,
    val frame: Color,
)

@Composable
private fun heroColors(isActive: Boolean): HeroColors {
    val scheme = MaterialTheme.colorScheme
    return if (isActive) {
        val highlight = highlightColors()
        HeroColors(
            container = highlight.container,
            content = highlight.content,
            secondaryContent = highlight.content,
            frame = highlight.content.copy(alpha = FRAME_ALPHA_ACTIVE),
        )
    } else {
        HeroColors(
            container = if (isDarkScheme()) scheme.surfaceContainerLow else scheme.surfaceContainerHigh,
            content = scheme.onSurface,
            secondaryContent = scheme.onSurfaceVariant,
            frame = scheme.outline.copy(alpha = FRAME_ALPHA_IDLE),
        )
    }
}

@Composable
fun PrayerHeroCard(hero: PrayerHero, is24Hour: Boolean, modifier: Modifier = Modifier) {
    val colors = heroColors(hero.isActive)
    val background = remember(colors, hero.isActive) { Modifier.mihrabBackground(colors, hero.isActive) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = HeroMinHeight)
            .then(background)
            .padding(start = 24.dp, top = 36.dp, end = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (hero.isActive) {
            StarBadge(color = colors.content, filled = true) { PrayerIcon(hero.prayer, tint = colors.container) }
        } else {
            StarBadge(color = colors.secondaryContent, filled = false) {
                PrayerIcon(hero.prayer, tint = colors.secondaryContent)
            }
        }
        StatusChip(hero.isActive, colors)
        HeroTitle(hero, colors)
        Spacer(modifier = Modifier.weight(1f))
        CountdownPill(hero, colors)
        HeroProgress(hero, is24Hour, colors.secondaryContent)
    }
}

private fun Modifier.mihrabBackground(colors: HeroColors, isActive: Boolean) = drawWithCache {
    val arch = mihrabPath(size, this, cornerRadius = 32.dp)
    val frame = mihrabPath(size, this, cornerRadius = 32.dp, inset = FrameInset)
    val dash = PathEffect.dashPathEffect(floatArrayOf(FrameDashOn.toPx(), FrameDashOff.toPx()))
    val frameStroke = Stroke(width = FrameStroke.toPx(), pathEffect = if (isActive) null else dash)
    val pattern = if (isActive) girihPath(size, this, PATTERN_DEPTH) else null
    val patternBrush = Brush.verticalGradient(
        colors = listOf(colors.content.copy(alpha = PATTERN_ALPHA), colors.content.copy(alpha = 0f)),
        startY = 0f,
        endY = size.height * PATTERN_DEPTH,
    )
    val patternStroke = Stroke(width = 1.dp.toPx())
    onDrawBehind {
        drawPath(arch, colors.container)
        if (pattern != null) {
            clipPath(arch) { drawPath(pattern, patternBrush, style = patternStroke) }
        }
        drawPath(frame, colors.frame, style = frameStroke)
    }
}

@Composable
private fun StatusChip(isActive: Boolean, colors: HeroColors) {
    val shape = RoundedCornerShape(14.dp)
    val content = if (isActive) colors.container else colors.secondaryContent
    val background = if (isActive) {
        Modifier.background(colors.content, shape)
    } else {
        Modifier.border(OutlineWidth, MaterialTheme.colorScheme.outline, shape)
    }
    Row(
        modifier = Modifier
            .padding(top = 12.dp)
            .height(28.dp)
            .then(background)
            .padding(start = 10.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LiveDot(color = content, isLive = isActive)
        Text(
            text = stringResource(if (isActive) R.string.base_other_prayer_now else R.string.base_other_no_prayer_now),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp,
            ),
            color = content,
            maxLines = 1,
        )
    }
}

@Composable
private fun LiveDot(color: Color, isLive: Boolean) {
    if (isLive) {
        val pulse = rememberInfiniteTransition(label = "pulse").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(durationMillis = PULSE_MILLIS, easing = LinearOutSlowInEasing)),
            label = "pulse",
        )
        Canvas(modifier = Modifier.size(8.dp)) {
            val radius = size.minDimension / 2
            val progress = pulse.value
            drawCircle(
                color = color.copy(alpha = PULSE_ALPHA * (1 - progress)),
                radius = radius * (1 + (PULSE_SCALE - 1) * progress),
            )
            drawCircle(color = color, radius = radius)
        }
    } else {
        Canvas(modifier = Modifier.size(8.dp)) {
            val stroke = 2.dp.toPx()
            drawCircle(color = color, radius = (size.minDimension - stroke) / 2, style = Stroke(width = stroke))
        }
    }
}

@Composable
private fun HeroTitle(hero: PrayerHero, colors: HeroColors) {
    if (!hero.isActive) {
        Text(
            text = stringResource(R.string.base_title_next_prayer),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.secondaryContent,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
    Text(
        text = stringResource(hero.prayer.nameResId),
        style = if (hero.isActive) {
            MaterialTheme.typography.displayLarge.copy(
                fontSize = 58.sp,
                lineHeight = 62.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1.2).sp,
            )
        } else {
            MaterialTheme.typography.displayMedium.copy(
                fontSize = 46.sp,
                lineHeight = 52.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.8).sp,
            )
        },
        color = colors.content,
        maxLines = 1,
        modifier = Modifier.padding(top = if (hero.isActive) 4.dp else 0.dp),
    )
    Text(
        text = stringResource(hero.prayer.arabicResId),
        style = MaterialTheme.typography.headlineSmall.copy(fontFamily = ArabicFontFamily, lineHeight = 34.sp),
        color = colors.secondaryContent,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun CountdownPill(hero: PrayerHero, colors: HeroColors) {
    val shape = RoundedCornerShape(20.dp)
    val background = if (hero.isActive) {
        Modifier.background(colors.content, shape)
    } else {
        Modifier.border(OutlineWidth, MaterialTheme.colorScheme.outline, shape)
    }
    Box(
        modifier = Modifier
            .height(40.dp)
            .then(background)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = countdownText(hero),
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
            color = if (hero.isActive) colors.container else colors.content,
            maxLines = 1,
        )
    }
}

@Composable
private fun countdownText(hero: PrayerHero): AnnotatedString {
    val duration = TimeUtils.formatCountdown(hero.secondsLeft)
    val template = stringResource(if (hero.isActive) R.string.base_other_until_end else R.string.base_other_until_start)
    val parts = template.split("%1\$s", limit = 2)
    return buildAnnotatedString {
        append(parts.first())
        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontFeatureSettings = TABULAR_NUMBERS)) { append(duration) }
        append(parts.getOrElse(1) { "" })
    }
}

@Composable
private fun HeroProgress(hero: PrayerHero, is24Hour: Boolean, color: Color) {
    val style = MaterialTheme.typography.titleSmall.copy(fontFeatureSettings = TABULAR_NUMBERS)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
    ) {
        Text(text = TimeUtils.formatMinutes(hero.startInMinutes, is24Hour), style = style, color = color, maxLines = 1)
        WavyProgress(
            progress = hero.progress,
            color = color,
            modifier = Modifier
                .weight(1f, fill = false)
                .widthIn(max = 236.dp)
                .fillMaxWidth()
                .height(14.dp),
        )
        Text(text = TimeUtils.formatMinutes(hero.endInMinutes, is24Hour), style = style, color = color, maxLines = 1)
    }
}
