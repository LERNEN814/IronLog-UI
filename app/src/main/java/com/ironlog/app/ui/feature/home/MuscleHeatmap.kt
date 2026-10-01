@file:OptIn(ExperimentalMaterial3Api::class)
package com.ironlog.app.ui.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clipToBounds
import com.ironlog.app.R
import com.ironlog.app.ui.theme.IronLogTheme

@Composable
fun MuscleHeatmap(model: HeatmapRenderModel, muscleNames: Map<String, String> = emptyMap(), modifier: Modifier = Modifier, onRegionClick: (String) -> Unit = {}, onViewChange: (BodyView) -> Unit = {}) {
    val view = model.view
    val selected = model.selectedMuscleId
    val scores = model.scores
    val regions = remember(view) { MusclePathTable.forView(view) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(view == BodyView.FRONT, { onViewChange(BodyView.FRONT) }, { Text(stringResource(R.string.heatmap_body_front)) })
            FilterChip(view == BodyView.BACK, { onViewChange(BodyView.BACK) }, { Text(stringResource(R.string.heatmap_body_back)) })
        }
        Column(Modifier.fillMaxWidth().aspectRatio(0.5f)) {
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
        val overlayWidth = maxWidth
        val overlayHeight = overlayWidth / 0.5f
        Canvas(Modifier.fillMaxSize().clearAndSetSemantics { }.pointerInput(view, regions) {
            detectTapGestures { point ->
                regions.asReversed().firstOrNull { it.interactive && PathGeometry.hitTest(it, point, size.width.toFloat(), size.height.toFloat()) }?.let {
                    onRegionClick(it.canonicalMuscleId)
                }
            }
        }) {
            regions.forEach { region ->
                PathGeometry.safeTransform(region, size.width, size.height)?.let { path ->
                    drawPath(path, HeatmapColors.colorFor(scores[region.canonicalMuscleId] ?: 0))
                    drawPath(path, if (selected == region.canonicalMuscleId) Color.White else Color(0xFF5F6368), style = Stroke(if (selected == region.canonicalMuscleId) 3f else 1f, cap = StrokeCap.Round))
                }
            }
        }
        regions.filter { it.interactive }.distinctBy { it.canonicalMuscleId }.forEach { region ->
            val label = muscleNames[region.canonicalMuscleId] ?: region.canonicalMuscleId
            val score = scores[region.canonicalMuscleId] ?: 0
            val regionDescription = stringResource(R.string.heatmap_region_description, label, score)
            val bounds = PathGeometry.safeTransform(region, overlayWidth.value * 10f, overlayHeight.value * 10f)?.getBounds()
            if (bounds != null) {
                // Keep neighboring TalkBack targets distinct while preserving a generous touch area.
                val targetWidth = (bounds.width / 10f).coerceIn(48f, 96f)
                val targetHeight = (bounds.height / 10f).coerceIn(48f, 96f)
                val targetLeft = ((bounds.center.x / 10f) - targetWidth / 2f)
                    .coerceIn(0f, (overlayWidth.value - targetWidth).coerceAtLeast(0f))
                val targetTop = ((bounds.center.y / 10f) - targetHeight / 2f)
                    .coerceIn(0f, (overlayHeight.value - targetHeight).coerceAtLeast(0f))
                Box(
                    Modifier
                        .offset(
                            targetLeft.dp,
                            targetTop.dp,
                        )
                        .size(
                            width = targetWidth.dp,
                            height = targetHeight.dp,
                        )
                        .clipToBounds()
                        .semantics {
                            contentDescription = regionDescription
                            role = Role.Button
                            onClick {
                                onRegionClick(region.canonicalMuscleId)
                                true
                            }
                        },
                )
            }
        }
        }
        }
        selected?.let { Text(stringResource(R.string.heatmap_stimulus, scores[it] ?: 0), style = MaterialTheme.typography.bodyMedium) }
        HeatmapLegend()
    }
}

@Composable private fun HeatmapLegend() {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(0 to R.string.heatmap_legend_none, 10 to R.string.heatmap_legend_low, 30 to R.string.heatmap_legend_mid, 60 to R.string.heatmap_legend_high, 90 to R.string.heatmap_legend_very_high).forEach { (score, label) ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { Text("■", color = HeatmapColors.colorFor(score)); Text(stringResource(label)) }
        }
    }
}

@Preview(showBackground = true)
@Composable private fun MuscleHeatmapPreview() { IronLogTheme { MuscleHeatmap(HeatmapRenderModel(BodyView.FRONT, mapOf("chest" to 50, "quads" to 75)), modifier = Modifier.padding(16.dp)) } }
