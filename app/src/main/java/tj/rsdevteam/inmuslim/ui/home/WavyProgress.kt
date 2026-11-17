package tj.rsdevteam.inmuslim.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

private const val TRACK_ALPHA = 0.28f

private val WaveStroke = 4.dp
private val WaveAmplitude = 3.dp
private val WaveLength = 20.dp
private val WaveGap = 6.dp
private val StopRadius = 2.dp

@Composable
fun WavyProgress(progress: Float, color: Color, modifier: Modifier = Modifier) {
    val value = progress.coerceIn(0f, 1f)
    Canvas(modifier = modifier.semantics { progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..1f) }) {
        val stroke = WaveStroke.toPx()
        val mid = size.height / 2
        val stop = size.width - StopRadius.toPx()
        val head = value * size.width
        val trackStart = min(stop, head + WaveGap.toPx())
        if (trackStart < stop - StopRadius.toPx()) {
            drawLine(
                color = color.copy(alpha = TRACK_ALPHA),
                start = Offset(trackStart, mid),
                end = Offset(stop, mid),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
        drawCircle(color = color, radius = StopRadius.toPx(), center = Offset(stop, mid))
        if (head > 1.dp.toPx()) {
            drawPath(
                path = wavePath(head, mid),
                color = color,
                style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
    }
}

private fun DrawScope.wavePath(length: Float, mid: Float): Path {
    val amplitude = WaveAmplitude.toPx()
    val wavelength = WaveLength.toPx()
    val step = 1.dp.toPx()
    return Path().apply {
        moveTo(0f, mid)
        var x = step
        while (x <= length) {
            lineTo(x, mid + amplitude * sin(x / wavelength * 2 * PI).toFloat())
            x += step
        }
    }
}
