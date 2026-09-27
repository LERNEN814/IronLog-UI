package com.ironlog.app.ui.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironlog.app.R
import com.ironlog.app.domain.model.SessionStatus
import com.ironlog.app.domain.model.WorkoutSession
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.summary.SessionSummary
import com.ironlog.app.ui.components.SessionCard
import com.ironlog.app.ui.feature.history.HistoryScreen
import com.ironlog.app.ui.feature.history.HistoryViewModel
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronLogTheme
import com.ironlog.app.ui.theme.regionColor
import java.time.LocalDate

/** Bottom-nav "历史" tab: calendar with a list-view toggle. */
@Composable
fun CalendarTabRoute(
    onOpenDetail: (String) -> Unit,
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel(),
) {
    val calendarState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    val historyState by historyViewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.ScreenPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = { if (calendarState.listMode) calendarViewModel.onListModeToggled() },
            ) {
                Text(
                    text = stringResource(R.string.calendar_view),
                    color = if (calendarState.listMode) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                )
            }
            TextButton(
                onClick = { if (!calendarState.listMode) calendarViewModel.onListModeToggled() },
            ) {
                Text(
                    text = stringResource(R.string.list_view),
                    color = if (calendarState.listMode) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
        if (calendarState.listMode) {
            HistoryScreen(
                state = historyState,
                onSessionClicked = onOpenDetail,
            )
        } else {
            CalendarScreen(
                state = calendarState,
                onPreviousMonth = calendarViewModel::onPreviousMonth,
                onNextMonth = calendarViewModel::onNextMonth,
                onToday = calendarViewModel::onToday,
                onDateSelected = calendarViewModel::onDateSelected,
                onOpenDetail = onOpenDetail,
            )
        }
    }
}

@Composable
fun CalendarScreen(
    state: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit,
    onDateSelected: (LocalDate?) -> Unit,
    onOpenDetail: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPadding),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.calendar_previous_month),
                )
            }
            Text(
                text = stringResource(R.string.calendar_year_month, state.year, state.month),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.calendar_next_month),
                )
            }
            TextButton(onClick = onToday) { Text(stringResource(R.string.calendar_today)) }
        }

        WeekdayHeader()

        state.days.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    DayCell(
                        day = day,
                        selected = day != null && state.selectedDate == day.date,
                        onClick = { onDateSelected(day?.date) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        val selectedDate = state.selectedDate
        if (selectedDate != null) {
            Text(
                text = selectedDate.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = Dimens.CardSpacing),
            )
            if (state.selectedSessions.isEmpty()) {
                Text(
                    text = stringResource(R.string.calendar_no_sessions),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                state.selectedSessions.forEach { header ->
                    SessionCard(header = header, onClick = { onOpenDetail(header.session.id) })
                }
            }
        }
    }
}

@Composable
private fun WeekdayHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        stringArrayOfWeekdays().forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f).padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun stringArrayOfWeekdays(): List<String> = listOf(
    stringResource(R.string.calendar_week_mon),
    stringResource(R.string.calendar_week_tue),
    stringResource(R.string.calendar_week_wed),
    stringResource(R.string.calendar_week_thu),
    stringResource(R.string.calendar_week_fri),
    stringResource(R.string.calendar_week_sat),
    stringResource(R.string.calendar_week_sun),
)

@Composable
private fun DayCell(
    day: CalendarDayUiState?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = when {
        selected -> MaterialTheme.colorScheme.primaryContainer
        day?.isToday == true -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable(enabled = day != null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (day != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = day.date.dayOfMonth.toString(), style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    day.dots.forEach { region ->
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(regionColor(region), CircleShape),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    IronLogTheme {
        CalendarScreen(
            state = CalendarUiState(
                today = LocalDate.of(2026, 9, 25),
                year = 2026,
                month = 9,
                days = com.ironlog.app.domain.calendar.CalendarModel.monthGrid(2026, 9).map { date ->
                    date?.let {
                        CalendarDayUiState(
                            date = it,
                            isToday = it == LocalDate.of(2026, 9, 25),
                            dots = if (it.dayOfMonth % 4 == 0) listOf(0, 4) else emptyList(),
                        )
                    }
                },
                selectedDate = LocalDate.of(2026, 9, 24),
                selectedSessions = listOf(
                    SessionHeader(
                        session = WorkoutSession(
                            id = "s1",
                            status = SessionStatus.FINISHED,
                            startedAt = 0,
                            endedAt = 3_600_000,
                            localDate = "2026-09-24",
                            rating = 8,
                            note = null,
                            restTargetAt = null,
                            createdAt = 0,
                            updatedAt = 0,
                        ),
                        exerciseNames = listOf("Bench Press", "Squat"),
                        bodyRegions = setOf(0, 4),
                        summary = SessionSummary(3_600, 2, 6, 2_000_000, setOf(0, 4)),
                    ),
                ),
            ),
            onPreviousMonth = {},
            onNextMonth = {},
            onToday = {},
            onDateSelected = {},
            onOpenDetail = {},
        )
    }
}
