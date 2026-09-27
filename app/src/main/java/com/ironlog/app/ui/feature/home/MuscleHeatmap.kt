@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironlog.app.R
import com.ironlog.app.ui.theme.IronLogTheme

/** M5-T5.2: simplified front/back body view coloured by fatigue score (DOMAIN_RULES §5.3). */
@Composable
fun MuscleHeatmap(
    scores: Map<String, Int>,
    modifier: Modifier = Modifier,
    onRegionClick: (String) -> Unit = {},
) {
    var body by remember { mutableIntStateOf(RegionGeometry.BODY_FRONT) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = body == RegionGeometry.BODY_FRONT,
                onClick = { body = RegionGeometry.BODY_FRONT },
                label = { Text(stringResource(R.string.heatmap_body_front)) },
            )
            FilterChip(
                selected = body == RegionGeometry.BODY_BACK,
                onClick = { body = RegionGeometry.BODY_BACK },
                label = { Text(stringResource(R.string.heatmap_body_back)) },
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.62f)
                .pointerInput(body) {
                    detectTapGestures { offset ->
                        val hit = RegionGeometry.forBody(body).firstOrNull { shape ->
                            val rect = shape.rect
                            offset.x >= rect.left * size.width &&
                                offset.x <= rect.right * size.width &&
                                offset.y >= rect.top * size.height &&
                                offset.y <= rect.bottom * size.height
                        }
                        hit?.let { onRegionClick(it.muscleId) }
                    }
                },
        ) {
            RegionGeometry.forBody(body).forEach { shape ->
                val left = shape.rect.left * size.width
                val top = shape.rect.top * size.height
                val width = (shape.rect.right - shape.rect.left) * size.width
                val height = (shape.rect.bottom - shape.rect.top) * size.height
                val radius = shape.cornerRadius * minOf(size.width, size.height)
                drawRoundRect(
                    color = HeatmapColors.colorFor(scores[shape.muscleId] ?: 0),
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(radius, radius),
                )
            }
        }
        HeatmapLegend()
    }
}

@Composable
private fun HeatmapLegend() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        LegendItem(0, stringResource(R.string.heatmap_legend_none))
        LegendItem(10, stringResource(R.string.heatmap_legend_low))
        LegendItem(30, stringResource(R.string.heatmap_legend_mid))
        LegendItem(60, stringResource(R.string.heatmap_legend_high))
        LegendItem(90, stringResource(R.string.heatmap_legend_very_high))
    }
}

@Composable
private fun LegendItem(sampleScore: Int, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(HeatmapColors.colorFor(sampleScore), RoundedCornerShape(3.dp)),
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
    Box(modifier = Modifier.width(0.dp))
}

@Preview(showBackground = true)
@Composable
private fun MuscleHeatmapPreview() {
    IronLogTheme {
        MuscleHeatmap(
            scores = mapOf(
                "chest" to 50,
                "triceps" to 25,
                "front_delts" to 25,
                "quads" to 75,
                "upper_back" to 10,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
