package com.ironlog.app.ui.feature.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironlog.app.R
import com.ironlog.app.core.units.UnitConverter
import com.ironlog.app.domain.timer.RestTimerUiModel
import com.ironlog.app.ui.theme.Dimens
import com.ironlog.app.ui.theme.IronEffects
import com.ironlog.app.ui.theme.IronGlassSurface
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
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val compact = maxWidth <= 380.dp
        IronGlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 88.dp else 76.dp),
            shape = RoundedCornerShape(
                topStart = IronEffects.OverlayCorner,
                topEnd = IronEffects.OverlayCorner,
            ),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = IronEffects.OverlayElevation,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp)) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 4.dp,
                    )
                    Text(
                        text = UnitConverter.formatDuration((model.remainingMillis / 1000L).toInt()),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    )
                }
                if (!model.isExact) {
                    Column(
                        modifier = Modifier.width(if (compact) 64.dp else 88.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        IconButton(
                            onClick = onInexactClick,
                            modifier = Modifier.size(Dimens.TouchTarget),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = stringResource(R.string.rest_inexact_hint),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Text(
                            text = stringResource(R.string.rest_inexact_short),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            maxLines = 2,
                            softWrap = true,
                        )
                    }
                } else {
                    Spacer(Modifier.width(if (compact) 64.dp else 88.dp))
                }
                OutlinedButton(
                    onClick = { onAdjust(-15) },
                    modifier = Modifier.weight(1f).height(Dimens.TouchTarget),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp),
                ) {
                    Text(stringResource(R.string.rest_minus_15), maxLines = 1)
                }
                OutlinedButton(
                    onClick = { onAdjust(+15) },
                    modifier = Modifier.weight(1f).height(Dimens.TouchTarget),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp),
                ) {
                    Text(stringResource(R.string.rest_plus_15), maxLines = 1)
                }
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.weight(0.8f).height(Dimens.TouchTarget),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp),
                ) {
                    Text(stringResource(R.string.rest_skip), maxLines = 1)
                }
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
