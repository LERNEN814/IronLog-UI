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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.clickable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironlog.app.R
import com.ironlog.app.ui.theme.IronLogTheme

@Composable
fun MuscleHeatmap(model: HeatmapRenderModel, muscleNames: Map<String, String> = emptyMap(), modifier: Modifier = Modifier, onRegionClick: (String) -> Unit = {}) {
    var view by remember(model.view) { mutableStateOf(model.view) }
    val selected = model.selectedMuscleId
    val scores = model.scores
    val regions = remember(view) { MusclePathTable.forView(view) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(view == BodyView.FRONT, { view = BodyView.FRONT }, { Text(stringResource(R.string.heatmap_body_front)) })
            FilterChip(view == BodyView.BACK, { view = BodyView.BACK }, { Text(stringResource(R.string.heatmap_body_back)) })
        }
        val description = stringResource(R.string.heatmap_accessibility_description)
        Column(Modifier.fillMaxWidth().aspectRatio(0.5f)) {
        Canvas(Modifier.fillMaxWidth().weight(1f).semantics { contentDescription = description }.pointerInput(view, regions) {
            detectTapGestures { point ->
                regions.asReversed().firstOrNull { it.interactive && PathGeometry.hitTest(it, point, size.width.toFloat(), size.height.toFloat()) }?.let {
                    onRegionClick(it.canonicalMuscleId)
                }
            }
        }) {
            val scale = minOf(size.width / MusclePathTable.VIEW_BOX_WIDTH, size.height / MusclePathTable.VIEW_BOX_HEIGHT)
            val dx = (size.width - MusclePathTable.VIEW_BOX_WIDTH * scale) / 2f
            val dy = (size.height - MusclePathTable.VIEW_BOX_HEIGHT * scale) / 2f
            regions.forEach { region ->
                val path = PathGeometry.parse(region)
                path.transform(androidx.compose.ui.graphics.Matrix().apply { scale(scale, scale); translate(dx / scale, dy / scale) })
                drawPath(path, HeatmapColors.colorFor(scores[region.canonicalMuscleId] ?: 0))
                drawPath(path, if (selected == region.canonicalMuscleId) Color.White else Color(0xFF5F6368), style = Stroke(if (selected == region.canonicalMuscleId) 3f else 1f, cap = StrokeCap.Round))
            }
        }
        }
        regions.filter { it.interactive }.distinctBy { it.canonicalMuscleId }.forEach { region ->
            val label = muscleNames[region.canonicalMuscleId] ?: region.canonicalMuscleId
            val score = scores[region.canonicalMuscleId] ?: 0
            val regionDescription = stringResource(R.string.heatmap_region_description, label, score)
            androidx.compose.foundation.layout.Spacer(
                Modifier.size(48.dp).semantics {
                    contentDescription = regionDescription
                    role = Role.Button
                }.clickable { onRegionClick(region.canonicalMuscleId) },
            )
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
