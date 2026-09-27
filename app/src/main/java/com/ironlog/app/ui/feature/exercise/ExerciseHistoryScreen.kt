@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.exercise

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironlog.app.R
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.strength.TrendPoint
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme

@Composable
fun ExerciseHistoryScreenRoute(
    exerciseId: String,
    onBack: () -> Unit,
    viewModel: ExerciseHistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ExerciseHistoryScreen(state = state, onBack = onBack)
}

@Composable
fun ExerciseHistoryScreen(state: ExerciseHistoryUiState, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exercise_history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
        ) {
            Text(text = state.exerciseName, style = MaterialTheme.typography.titleLarge)
            Text(
                text = stringResource(R.string.exercise_history_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.trend.isEmpty()) {
                Text(
                    text = stringResource(R.string.exercise_history_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    TrendChart(
                        trend = state.trend,
                        prDates = state.prDates,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(Dimens.ScreenPadding),
                    )
                }
                state.trend.reversed().forEach { point ->
                    TrendRow(point = point, isPr = point.date in state.prDates, displayUnit = state.displayUnit)
                }
            }
        }
    }
}

@Composable
private fun TrendChart(trend: List<TrendPoint>, prDates: Set<String>, modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.primary
    val prColor = MaterialTheme.colorScheme.error
    val baselineColor = MaterialTheme.colorScheme.outline
    Canvas(modifier = modifier) {
        if (trend.isEmpty()) return@Canvas
        val maxValue = trend.maxOf { it.bestE1rmGrams }.toFloat()
        val minValue = trend.minOf { it.bestE1rmGrams }.toFloat()
        val range = (maxValue - minValue).coerceAtLeast(1f)
        val chartHeight = size.height - 20f
        val stepX = if (trend.size > 1) size.width / (trend.size - 1) else 0f
        val points = trend.mapIndexed { index, point ->
            Offset(
                x = stepX * index,
                y = size.height - ((point.bestE1rmGrams - minValue) / range) * chartHeight - 10f,
            )
        }
        drawLine(baselineColor, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 2f)
        points.zipWithNext { from, to ->
            drawLine(lineColor, from, to, strokeWidth = 4f)
        }
        trend.forEachIndexed { index, point ->
            val isPr = point.date in prDates
            drawCircle(
                color = if (isPr) prColor else lineColor,
                radius = if (isPr) 9f else 5f,
                center = points[index],
            )
        }
    }
}

@Composable
private fun TrendRow(point: TrendPoint, isPr: Boolean, displayUnit: com.ironlog.app.domain.model.WeightUnit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isPr) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = point.date, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.weight(1f))
        Text(
            text = UnitConverter.formatGrams(point.bestE1rmGrams, displayUnit) +
                stringResource(if (displayUnit == com.ironlog.app.domain.model.WeightUnit.KG) R.string.unit_kg else R.string.unit_lb),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.exercise_history_volume) + " " +
                UnitConverter.formatGrams(point.volumeGrams.toInt(), displayUnit),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (isPr) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = stringResource(R.string.exercise_history_pr),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
        }
        Spacer(Modifier.width(2.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseHistoryScreenPreview() {
    IronLogTheme {
        ExerciseHistoryScreen(
            state = ExerciseHistoryUiState(
                loading = false,
                exerciseName = "Bench Press",
                trend = listOf(
                    TrendPoint("2026-09-01", 100_000, 800_000),
                    TrendPoint("2026-09-08", 105_000, 900_000),
                    TrendPoint("2026-09-15", 102_000, 850_000),
                ),
                prDates = setOf("2026-09-01", "2026-09-08"),
            ),
            onBack = {},
        )
    }
}
