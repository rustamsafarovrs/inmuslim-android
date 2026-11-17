package tj.rsdevteam.inmuslim.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

private const val SKELETON_ROWS = 5
private const val SHIMMER_MIN_ALPHA = 0.55f
private const val SHIMMER_HALF_MILLIS = 700
private const val SHIMMER_STAGGER_MILLIS = 80

@Composable
fun HomeSkeleton(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.surfaceContainerHigh
    Column(modifier = modifier) {
        ShimmerBlock(
            shape = MihrabShape(),
            color = color,
            delayMillis = 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(344.dp),
        )
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            repeat(SKELETON_ROWS) { index ->
                ShimmerBlock(
                    shape = segmentShape(index, SKELETON_ROWS),
                    color = color,
                    delayMillis = index * SHIMMER_STAGGER_MILLIS,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                )
            }
        }
    }
}

@Composable
private fun ShimmerBlock(shape: Shape, color: Color, delayMillis: Int, modifier: Modifier = Modifier) {
    val alpha = rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = SHIMMER_MIN_ALPHA,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_HALF_MILLIS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(delayMillis),
        ),
        label = "alpha",
    )
    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha.value }
            .background(color, shape),
    )
}
