package tj.rsdevteam.inmuslim.ui.home

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

private val ShoulderHeight = 150.dp
private val ArchSpread = 72.dp
private val ArchDip = 18.dp

private val TileSize = 38.dp
private val TileOffsetX = 1.dp
private val TileOffsetY = (-6).dp
private val TileSquareInset = 12.dp
private val TileSquareSide = 14.dp
private val TileSpoke = 7.dp

class MihrabShape(private val cornerRadius: Dp = 32.dp) : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(mihrabPath(size, density, cornerRadius))
    }
}

fun mihrabPath(size: Size, density: Density, cornerRadius: Dp, inset: Dp = 0.dp): Path = with(density) {
    val width = size.width
    val mid = width / 2
    val shoulder = ShoulderHeight.toPx()
    val spread = ArchSpread.toPx()
    val dip = ArchDip.toPx()
    val gap = inset.toPx()
    val left = gap
    val right = width - gap
    val bottom = size.height - gap
    val corner = cornerRadius.toPx() - gap
    // Offsetting the arch's control polygon keeps the inset curve parallel to the outer one.
    val side = InsetEdge(Offset(0f, shoulder), Offset(0f, spread), gap)
    val slope = InsetEdge(Offset(0f, spread), Offset(mid - spread, dip), gap)
    val ridge = InsetEdge(Offset(mid - spread, dip), Offset(mid, 0f), gap)
    val axis = InsetEdge(Offset(mid, 0f), Offset(mid, bottom), 0f)
    val control1 = side.intersect(slope)
    val control2 = slope.intersect(ridge)
    val apex = ridge.intersect(axis)
    Path().apply {
        moveTo(left, shoulder)
        cubicTo(control1.x, control1.y, control2.x, control2.y, apex.x, apex.y)
        cubicTo(width - control2.x, control2.y, width - control1.x, control1.y, right, shoulder)
        lineTo(right, bottom - corner)
        quadraticTo(right, bottom, right - corner, bottom)
        lineTo(left + corner, bottom)
        quadraticTo(left, bottom, left, bottom - corner)
        close()
    }
}

private class InsetEdge(from: Offset, to: Offset, inset: Float) {
    private val direction = to - from
    private val origin = from + Offset(-direction.y, direction.x) * (inset / direction.getDistance())

    fun intersect(other: InsetEdge): Offset {
        val t = cross(other.origin - origin, other.direction) / cross(direction, other.direction)
        return origin + direction * t
    }

    private fun cross(a: Offset, b: Offset) = a.x * b.y - a.y * b.x
}

fun girihPath(size: Size, density: Density, depth: Float): Path {
    val tile = with(density) { TileSize.toPx() }
    val columns = generateSequence(with(density) { TileOffsetX.toPx() } - tile) { it + tile }
        .takeWhile { it < size.width }
        .toList()
    val rows = generateSequence(with(density) { TileOffsetY.toPx() }) { it + tile }
        .takeWhile { it < size.height * depth }
    val path = Path()
    for (y in rows) {
        for (x in columns) path.addGirihTile(Offset(x, y), tile, density)
    }
    return path
}

private fun Path.addGirihTile(origin: Offset, tile: Float, density: Density) = with(density) {
    val inset = TileSquareInset.toPx()
    val side = TileSquareSide.toPx()
    val spoke = TileSpoke.toPx()
    val center = origin + Offset(tile / 2, tile / 2)
    val diamond = side * sqrt(2f) / 2
    addRect(Rect(origin + Offset(inset, inset), Size(side, side)))
    moveTo(center.x, center.y - diamond)
    lineTo(center.x + diamond, center.y)
    lineTo(center.x, center.y + diamond)
    lineTo(center.x - diamond, center.y)
    close()
    moveTo(center.x, origin.y)
    lineTo(center.x, origin.y + spoke)
    moveTo(center.x, origin.y + tile - spoke)
    lineTo(center.x, origin.y + tile)
    moveTo(origin.x, center.y)
    lineTo(origin.x + spoke, center.y)
    moveTo(origin.x + tile - spoke, center.y)
    lineTo(origin.x + tile, center.y)
}
