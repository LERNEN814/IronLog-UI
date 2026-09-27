package com.ironlog.app.ui.feature.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironlog.app.R
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.timer.RestTimerUiModel
import com.ironlog.app.ui.theme.IronLogTheme

/** REST_TIMER section 6 / R-3.2: fixed bar at the bottom of the session screen. */
@Composable
fun RestTimerBar(
    model: RestTimerUiModel,
    onAdjust: (Int) -> Unit,
    onSkip: () -> Unit,
    onInexactClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = if (model.totalMillis > 0L) {
        (model.remainingMillis.toFloat() / model.totalMillis.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 4.dp,
                )
                Text(
                    text = UnitConverter.formatDuration((model.remainingMillis / 1000L).toInt()),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            if (!model.isExact) {
                IconButton(onClick = onInexactClick) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = stringResource(R.string.rest_inexact_hint),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            OutlinedButton(onClick = { onAdjust(-15) }) {
                Text(stringResource(R.string.rest_minus_15))
            }
            OutlinedButton(onClick = { onAdjust(+15) }) {
                Text(stringResource(R.string.rest_plus_15))
            }
            TextButton(onClick = onSkip) {
                Text(stringResource(R.string.rest_skip))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RestTimerBarPreview() {
    IronLogTheme {
        RestTimerBar(
            model = RestTimerUiModel(
                sessionId = "session-1",
                remainingMillis = 65_000L,
                totalMillis = 90_000L,
                isExact = false,
                isRunning = true,
            ),
            onAdjust = {},
            onSkip = {},
            onInexactClick = {},
        )
    }
}
