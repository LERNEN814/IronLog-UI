package com.ironlog.app.ui.feature.summary

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ironlog.app.R
import com.ironlog.app.ui.components.PlaceholderScreen
import com.ironlog.app.ui.theme.IronLogTheme

/** The M2 flow shows the summary in a sheet; this destination is for later deep links. */
@Composable
fun SessionSummaryScreen(onBack: () -> Unit) {
    PlaceholderScreen(stringResource(R.string.placeholder_summary))
}

@Preview(showBackground = true)
@Composable
private fun SessionSummaryScreenPreview() {
    IronLogTheme {
        SessionSummaryScreen(onBack = {})
    }
}
