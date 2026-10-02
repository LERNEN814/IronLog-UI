@file:OptIn(ExperimentalMaterial3Api::class)

package com.ironlog.app.ui.feature.summary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ironlog.app.R
import com.ironlog.app.ui.theme.IronLogTheme
import com.ironlog.app.ui.theme.IronEffects
import com.ironlog.app.ui.theme.IronGlassSurface
import com.ironlog.app.ui.theme.Dimens
import androidx.compose.ui.unit.dp

/** The M2 flow shows the summary in a sheet; this destination is for later deep links. */
@Composable
fun SessionSummaryScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.summary_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.SectionGap),
        ) {
            Text(stringResource(R.string.summary_title), style = MaterialTheme.typography.headlineMedium)
            IronGlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(IronEffects.PrimaryCorner),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = IronEffects.RaisedElevation,
            ) {
              Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.summary_no_data), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.summary_open_from_session), color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_back)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionSummaryScreenPreview() {
    IronLogTheme {
        SessionSummaryScreen(onBack = {})
    }
}
