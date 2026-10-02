@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.bodyweight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironlog.app.R
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronEffects
import com.ironlog.app.ui.theme.IronGlassSurface

@Composable
fun BodyWeightScreenRoute(onBack: () -> Unit, viewModel: BodyWeightViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BodyWeightScreen(state, viewModel::save, viewModel::delete, onBack)
}

@Composable
fun BodyWeightScreen(state: BodyWeightUiState, onSave: (Int) -> Unit, onDelete: (String) -> Unit, onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.body_weight_title)) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.action_back)) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(Dimens.ScreenPadding), verticalArrangement = Arrangement.spacedBy(Dimens.SectionGap)) {
            Text(stringResource(R.string.body_weight_today, state.today), style = MaterialTheme.typography.titleMedium)
            IronGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.CardCorner),
                shadowElevation = IronEffects.RaisedElevation,
            ) {
            Row(Modifier.fillMaxWidth().padding(Dimens.ScreenPadding), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(input, { input = it }, Modifier.weight(1f), label = { Text(stringResource(R.string.body_weight_input)) }, singleLine = true)
                Button(onClick = { UnitConverter.parseToGrams(input, state.unit)?.let(onSave); input = "" }) { Text(stringResource(R.string.action_save)) }
            }
            }
            BodyWeightTrend(state.entries)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(state.entries, key = { it.id }) { entry ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(entry.localDate)
                        Text(UnitConverter.formatGrams(entry.weightGrams, state.unit))
                        IconButton(onClick = { onDelete(entry.localDate) }) { Icon(Icons.Filled.Delete, stringResource(R.string.action_delete)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun BodyWeightTrend(entries: List<com.ironlog.app.domain.model.BodyWeight>) {
    val points = entries.sortedBy { it.localDate }.takeLast(30)
    if (points.size < 2) return
    val values = points.map { it.weightGrams.toFloat() }
    val minValue = values.minOrNull() ?: return
    val range = (values.maxOrNull() ?: minValue) - minValue
    val lineColor = MaterialTheme.colorScheme.primary
    Canvas(Modifier.fillMaxWidth().height(120.dp)) {
        val horizontalPadding = 8.dp.toPx()
        val verticalPadding = 12.dp.toPx()
        val usableWidth = size.width - horizontalPadding * 2
        val usableHeight = size.height - verticalPadding * 2
        val normalizedRange = range.takeIf { it > 0f } ?: 1f
        val coordinates = values.mapIndexed { index, value ->
            Offset(
                x = horizontalPadding + usableWidth * index / (values.size - 1),
                y = size.height - verticalPadding - (value - minValue) / normalizedRange * usableHeight,
            )
        }
        coordinates.zipWithNext().forEach { (start, end) ->
            drawLine(lineColor, start, end, strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
        }
        coordinates.forEach { point ->
            drawCircle(lineColor, radius = 4.dp.toPx(), center = point)
        }
    }
}
