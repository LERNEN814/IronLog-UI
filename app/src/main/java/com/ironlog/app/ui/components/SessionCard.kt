package com.ironlog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironlog.app.R
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.summary.SessionHeader
import com.ironlog.app.domain.summary.SessionListFormatter
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.regionColor

/** Session list card shared by home and history. */
@Composable
fun SessionCard(header: SessionHeader, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = header.session.localDate, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.weight(1f))
                Text(
                    text = UnitConverter.formatDuration(header.summary.durationS.toInt()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = exerciseNamesText(header.exerciseNames),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                header.bodyRegions.sorted().take(4).forEach { region ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(regionColor(region), CircleShape),
                    )
                }
                Spacer(Modifier.weight(1f))
                val rating = header.session.rating
                Text(
                    text = if (rating == null) {
                        stringResource(R.string.session_not_rated)
                    } else {
                        stringResource(R.string.session_rating, rating)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun exerciseNamesText(names: List<String>): String {
    val summary = SessionListFormatter.exerciseSummary(names)
    val separator = stringResource(R.string.list_separator)
    val joined = summary.shownNames.joinToString(separator)
    return if (summary.hiddenCount > 0) {
        stringResource(R.string.exercise_summary_overflow, joined, summary.hiddenCount)
    } else {
        joined
    }
}
