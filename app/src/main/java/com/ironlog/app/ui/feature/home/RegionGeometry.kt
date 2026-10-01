package com.ironlog.app.ui.feature.home

import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.asAndroidPath

data class RegionShape(
    val muscleId: String,
    val body: Int,
    val rect: RectF,
    val cornerRadius: Float = 0f,
)

object RegionGeometry {
    const val BODY_FRONT = 0
    const val BODY_BACK = 1
    fun view(body: Int) = if (body == BODY_BACK) BodyView.BACK else BodyView.FRONT
    fun forBody(body: Int): List<PathRegion> = MusclePathTable.forView(view(body))
}

internal object PathGeometry {
    fun parse(region: PathRegion): Path = PathParser().parsePathString(region.pathData).toPath()

    fun transform(source: Path, width: Float, height: Float): Path {
        val scale = minOf(width / MusclePathTable.VIEW_BOX_WIDTH, height / MusclePathTable.VIEW_BOX_HEIGHT)
        val dx = (width - MusclePathTable.VIEW_BOX_WIDTH * scale) / 2f
        val dy = (height - MusclePathTable.VIEW_BOX_HEIGHT * scale) / 2f
        return Path().also { out -> out.addPath(source) }.apply {
            transform(androidx.compose.ui.graphics.Matrix().apply { translate(dx, dy); scale(scale, scale) })
        }
    }

    fun contains(path: Path, point: Offset): Boolean {
        val bounds = path.getBounds()
        if (!bounds.contains(point)) return false
        val region = android.graphics.Region()
        val clip = android.graphics.Region(
            bounds.left.toInt(), bounds.top.toInt(),
            kotlin.math.ceil(bounds.right).toInt(), kotlin.math.ceil(bounds.bottom).toInt(),
        )
        region.setPath(path.asAndroidPath(), clip)
        return region.contains(point.x.toInt(), point.y.toInt())
    }

    fun hitTest(region: PathRegion, point: Offset, width: Float, height: Float): Boolean =
        contains(transform(parse(region), width, height), point)
}
