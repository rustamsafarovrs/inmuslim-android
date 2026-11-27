package tj.rsdevteam.inmuslim.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

private const val STAR_SQUARE_FRACTION = 32f / 48f
private const val DARK_LUMINANCE = 0.5f

private val StarCorner = 5.dp
private val StarStroke = 2.dp

val ArabicFontFamily = FontFamily.Serif
const val TABULAR_NUMBERS = "tnum"

@Immutable
data class HighlightColors(val container: Color, val content: Color)

@Composable
fun isDarkScheme(): Boolean = MaterialTheme.colorScheme.background.luminance() < DARK_LUMINANCE

@Composable
fun highlightColors(): HighlightColors {
    val scheme = MaterialTheme.colorScheme
    return if (isDarkScheme()) {
        HighlightColors(container = scheme.primaryContainer, content = scheme.onPrimaryContainer)
    } else {
        HighlightColors(container = scheme.primary, content = scheme.onPrimary)
    }
}

fun segmentShape(index: Int, count: Int): RoundedCornerShape {
    val top = if (index == 0) 24.dp else 6.dp
    val bottom = if (index == count - 1) 24.dp else 6.dp
    return RoundedCornerShape(topStart = top, topEnd = top, bottomEnd = bottom, bottomStart = bottom)
}

@Composable
fun StarBadge(
    color: Color,
    filled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .drawBehind { drawStar(color, filled) },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private fun DrawScope.drawStar(color: Color, filled: Boolean) {
    val side = size.minDimension * STAR_SQUARE_FRACTION
    val topLeft = Offset((size.width - side) / 2, (size.height - side) / 2)
    val corner = CornerRadius(StarCorner.toPx())
    val style = if (filled) Fill else Stroke(width = StarStroke.toPx())
    drawRoundRect(color = color, topLeft = topLeft, size = Size(side, side), cornerRadius = corner, style = style)
    rotate(degrees = 45f) {
        drawRoundRect(color = color, topLeft = topLeft, size = Size(side, side), cornerRadius = corner, style = style)
    }
}

@Composable
fun PrayerIcon(prayer: Prayer, tint: Color, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(prayer.iconResId),
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(24.dp),
    )
}
