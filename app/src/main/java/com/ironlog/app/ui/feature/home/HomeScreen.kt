package com.ironlog.app.ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironlog.app.R
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.summary.DurationText
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.summary.SessionSummary
import com.ironlog.app.domain.summary.WeekStats
import com.ironlog.app.ui.components.SessionCard
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme
import com.ironlog.app.ui.theme.regionColor
import kotlinx.coroutines.delay

@Composable
fun HomeScreenRoute(
    onOpenSession: (String) -> Unit,
    onOpenHistory: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.inProgress?.id) {
        if (state.inProgress != null) {
            while (true) {
                viewModel.refreshElapsed()
                delay(1000L)
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.OpenSession -> onOpenSession(event.sessionId)
            }
        }
    }

    LaunchedEffect(Unit) { viewModel.refreshFatigue() }

    HomeScreen(
        state = state,
        onPrimaryAction = viewModel::onPrimaryAction,
        onOpenHistory = onOpenHistory,
        onMuscleSelected = viewModel::onMuscleSelected,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onPrimaryAction: () -> Unit,
    onOpenHistory: () -> Unit,
    onMuscleSelected: (String?) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing),
    ) {
        item {
            Button(
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth().height(72.dp),
                shape = MaterialTheme.shapes.large,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val label = if (state.primaryAction == HomePrimaryAction.RESUME) {
                        stringResource(R.string.home_continue_workout, DurationText.mmss(state.elapsedSeconds))
                    } else {
                        stringResource(R.string.home_start_workout)
                    }
                    Text(text = label, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "力量训练 · 有氧训练 · 休息",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    )
                }
            }
        }
        item { WeekStatsCard(state.weekStats) }
        item {
            HeatmapCard(
                scores = state.fatigueScores,
                muscleNames = state.muscleNames,
                selectedMuscle = state.selectedMuscle,
                onMuscleSelected = onMuscleSelected,
            )
        }
        item {
            Text(
                text = stringResource(R.string.home_recent_title),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        if (state.recentSessions.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.home_no_sessions),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(state.recentSessions, key = { it.session.id }) { header ->
            SessionCard(header = header, onClick = onOpenHistory)
        }
    }
}

@Composable
private fun WeekStatsCard(stats: WeekStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatCell(label = stringResource(R.string.home_week_sessions), value = stats.sessionCount.toString())
            StatCell(label = stringResource(R.string.home_week_sets), value = stats.workSetCount.toString())
        }
    }
}

@Composable
private fun StatCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun HeatmapCard(
    scores: Map<String, Int>,
    muscleNames: Map<String, String>,
    selectedMuscle: String?,
    onMuscleSelected: (String?) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.home_heatmap_title),
                style = MaterialTheme.typography.titleMedium,
            )
            MuscleHeatmap(
                scores = scores,
                onRegionClick = { muscleId ->
                    onMuscleSelected(if (selectedMuscle == muscleId) null else muscleId)
                },
            )
            val selected = selectedMuscle
            if (selected != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = muscleNames[selected] ?: selected,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.heatmap_stimulus, scores[selected] ?: 0),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    IronLogTheme {
        HomeScreen(
            state = HomeUiState(
                loading = false,
                primaryAction = HomePrimaryAction.RESUME,
                elapsedSeconds = 754,
                weekStats = WeekStats(sessionCount = 3, workSetCount = 42),
                recentSessions = listOf(
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
                            updatedAt = 3_600_000,
                        ),
                        exerciseNames = listOf("Barbell Bench Press", "Squat", "Deadlift", "Row"),
                        bodyRegions = setOf(0, 4),
                        summary = SessionSummary(
                            durationS = 3600,
                            exerciseCount = 4,
                            workSetCount = 12,
                            totalVolumeGrams = 4_000_000,
                            bodyRegions = setOf(0, 4),
                        ),
                    ),
                ),
            ),
            onPrimaryAction = {},
            onOpenHistory = {},
            onMuscleSelected = {},
        )
    }
}
