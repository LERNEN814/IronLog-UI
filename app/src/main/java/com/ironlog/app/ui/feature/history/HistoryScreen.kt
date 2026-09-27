@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironlog.app.R
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.SetType
import com.ironlog.app.domain.model.WeightUnit
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.model.WorkoutSet
import com.ironlog.app.domain.summary.SessionDetail
import com.ironlog.app.domain.summary.SessionExerciseWithSets
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.summary.SessionSummary
import com.ironlog.app.ui.components.SessionCard
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme

@Composable
fun HistoryScreenRoute(viewModel: HistoryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HistoryScreen(state = state, onSessionClicked = {})
}

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onSessionClicked: (String) -> Unit,
) {
    var selectedTab by remember { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxSize()) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
        ) {
            SegmentedButton(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                label = { Text(stringResource(R.string.calendar_view)) },
            )
            SegmentedButton(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                icon = { Icon(Icons.Filled.Insights, contentDescription = null) },
                label = { Text(stringResource(R.string.history_stats)) },
            )
        }
        if (selectedTab == 1) {
            HistoryStats(state.sessions)
        } else if (state.sessions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(Dimens.ScreenPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.history_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimens.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
            ) {
                items(state.sessions, key = { it.session.id }) { header ->
                    SessionCard(header = header, onClick = { onSessionClicked(header.session.id) })
                }
            }
        }
    }
}

@Composable
private fun HistoryStats(sessions: List<SessionHeader>) {
    val totalSets = sessions.sumOf { it.summary.workSetCount }
    val totalMinutes = sessions.sumOf { it.summary.durationS } / 60
    Column(
        modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
    ) {
        Text(stringResource(R.string.history_stats_title), style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.CardSpacing)) {
            StatTile(modifier = Modifier.weight(1f), value = sessions.size.toString(), label = stringResource(R.string.home_week_sessions))
            StatTile(modifier = Modifier.weight(1f), value = totalSets.toString(), label = stringResource(R.string.home_week_sets))
            StatTile(modifier = Modifier.weight(1f), value = totalMinutes.toString(), label = stringResource(R.string.history_minutes))
        }
    }
}

@Composable
private fun StatTile(modifier: Modifier = Modifier, value: String, label: String) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(Dimens.CardCorner))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    IronLogTheme {
        HistoryScreen(
            state = HistoryUiState(
                loading = false,
                sessions = listOf(
                    SessionHeader(
                        session = WorkoutSession(
                            id = "s1",
                            status = SessionStatus.FINISHED,
                            startedAt = 0,
                            endedAt = 3_600_000,
                            localDate = "2026-09-25",
                            rating = 8,
                            note = null,
                            restTargetAt = null,
                            createdAt = 0,
                            updatedAt = 0,
                        ),
                        exerciseNames = listOf("Bench Press", "Squat", "Deadlift", "Row"),
                        bodyRegions = setOf(0, 4),
                        summary = SessionSummary(3_600, 4, 12, 4_000_000, setOf(0, 4)),
                    ),
                ),
            ),
            onSessionClicked = {},
        )
    }
}
