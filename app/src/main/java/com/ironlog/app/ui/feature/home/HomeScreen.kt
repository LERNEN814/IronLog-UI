package com.ironlog.app.ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.ironlog.app.ui.theme.IronGlassSurface
import com.ironlog.app.ui.theme.IronEffects
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
        onBodyViewSelected = viewModel::onBodyViewSelected,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onPrimaryAction: () -> Unit,
    onOpenHistory: () -> Unit,
    onMuscleSelected: (String?) -> Unit,
    onBodyViewSelected: (BodyView) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Dimens.ScreenPadding,
            top = Dimens.ScreenPadding,
            end = Dimens.ScreenPadding,
            bottom = Dimens.TouchTarget + Dimens.ScreenPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SectionGap),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = stringResource(R.string.home_greeting), style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = stringResource(R.string.home_training_types),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            Button(
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth().height(72.dp),
                shape = RoundedCornerShape(Dimens.CardCorner),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    val label = if (state.primaryAction == HomePrimaryAction.RESUME) {
                        stringResource(R.string.home_continue_workout, DurationText.mmss(state.elapsedSeconds))
                    } else {
                        stringResource(R.string.home_start_workout)
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(text = stringResource(R.string.home_action_hint), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        item {
            SectionHeader(
                title = stringResource(R.string.home_week_title),
                action = stringResource(R.string.home_view_history),
                onAction = onOpenHistory,
            )
        }
        item { WeekStatsCard(state.weekStats) }
        item {
            HeatmapCard(
                muscleNames = state.muscleNames,
                heatmap = state.heatmap,
                onMuscleSelected = onMuscleSelected,
                onBodyViewSelected = onBodyViewSelected,
            )
        }
        item {
            SectionHeader(
                title = stringResource(R.string.home_recent_title),
                action = stringResource(R.string.home_view_history),
                onAction = onOpenHistory,
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
    IronGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CardCorner),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f),
        shadowElevation = IronEffects.RaisedElevation,
    ) {
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
    muscleNames: Map<String, String>,
    heatmap: HeatmapState,
    onMuscleSelected: (String?) -> Unit,
    onBodyViewSelected: (BodyView) -> Unit,
) {
    IronGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CardCorner),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = IronEffects.RaisedElevation,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.home_heatmap_title),
                style = MaterialTheme.typography.titleMedium,
            )
            when (heatmap) {
                HeatmapState.Loading -> Text(stringResource(R.string.heatmap_loading))
                HeatmapState.Empty -> Text(stringResource(R.string.heatmap_empty))
                is HeatmapState.Error -> Text(stringResource(R.string.heatmap_error))
                is HeatmapState.Ready -> if (heatmap.cardioScore > 0) Text(stringResource(R.string.heatmap_cardio, heatmap.cardioScore))
            }
            if (heatmap is HeatmapState.Ready) {
                MuscleHeatmap(
                    model = heatmap.model,
                    muscleNames = muscleNames,
                    onViewChange = onBodyViewSelected,
                    onRegionClick = { muscleId ->
                        onMuscleSelected(if (heatmap.model.selectedMuscleId == muscleId) null else muscleId)
                    },
                )
            }
            val readyHeatmap = heatmap as? HeatmapState.Ready
            val selected = readyHeatmap?.model?.selectedMuscleId
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
                        text = stringResource(R.string.heatmap_stimulus, readyHeatmap.model.scores[selected] ?: 0),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = action,
            modifier = Modifier
                .heightIn(min = Dimens.TouchTarget)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                .clickable(onClick = onAction)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
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
