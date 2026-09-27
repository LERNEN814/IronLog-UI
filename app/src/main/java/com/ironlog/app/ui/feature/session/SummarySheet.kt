@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironlog.app.R
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.summary.SessionSummary
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme

private val RATINGS = (5..10).toList()

@Composable
fun SummarySheet(
    summary: SessionSummary?,
    displayUnit: WeightUnit,
    rating: Int?,
    note: String,
    onRatingSelected: (Int) -> Unit,
    onNoteChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onKeepTraining: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onKeepTraining, sheetState = sheetState) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
        ) {
            Text(
                text = stringResource(R.string.summary_title),
                style = MaterialTheme.typography.titleMedium,
            )
            val safeSummary = summary ?: SessionSummary(0, 0, 0, 0, emptySet())
            SummaryRow(stringResource(R.string.summary_duration), UnitConverter.formatDuration(safeSummary.durationS.toInt()))
            SummaryRow(stringResource(R.string.summary_exercises), safeSummary.exerciseCount.toString())
            SummaryRow(stringResource(R.string.summary_work_sets), safeSummary.workSetCount.toString())
            SummaryRow(
                label = stringResource(R.string.summary_volume),
                value = UnitConverter.formatGrams(safeSummary.totalVolumeGrams.toInt(), displayUnit) +
                    stringResource(unitSuffixRes(displayUnit)),
            )
            Text(
                text = stringResource(R.string.summary_rating),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RATINGS.forEach { value ->
                    FilterChip(
                        selected = rating == value,
                        onClick = { onRatingSelected(value) },
                        label = { Text(value.toString()) },
                    )
                }
            }
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.summary_note)) },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.CardSpacing)) {
                TextButton(onClick = onKeepTraining, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.summary_continue))
                }
                Button(onClick = onConfirm, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.summary_confirm))
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun unitSuffixRes(unit: WeightUnit): Int =
    if (unit == WeightUnit.KG) R.string.unit_kg else R.string.unit_lb

@Preview(showBackground = true)
@Composable
private fun SummarySheetContentPreview() {
    IronLogTheme {
        Column(
            modifier = Modifier.padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
        ) {
            Text(stringResource(R.string.summary_title), style = MaterialTheme.typography.titleMedium)
            SummaryRow(stringResource(R.string.summary_duration), UnitConverter.formatDuration(3_723))
            SummaryRow(stringResource(R.string.summary_volume), "14.4 kg")
        }
    }
}
