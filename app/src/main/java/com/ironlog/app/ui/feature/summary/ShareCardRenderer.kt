package com.ironlog.app.ui.feature.summary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * M5-T5.3: renders the share card off-screen. Uses a plain [View] + `View.draw(Canvas)`
 * (pre-approved fallback in docs/audit/M0.md) instead of `GraphicsLayer.toImageBitmap()`,
 * which is not usable under Robolectric.
 */
object ShareCardRenderer {

    fun render(context: Context, data: ShareCardData): Bitmap {
        val view = ShareCardView(context).apply { this.data = data }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(ShareCardView.CARD_WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(ShareCardView.CARD_HEIGHT, View.MeasureSpec.EXACTLY),
        )
        view.layout(0, 0, ShareCardView.CARD_WIDTH, ShareCardView.CARD_HEIGHT)
        val bitmap = Bitmap.createBitmap(
            ShareCardView.CARD_WIDTH,
            ShareCardView.CARD_HEIGHT,
            Bitmap.Config.ARGB_8888,
        )
        view.draw(Canvas(bitmap))
        return bitmap
    }
}

/** In-app preview of the same card. */
@Composable
fun ShareCard(data: ShareCardData, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context -> ShareCardView(context).apply { this.data = data } },
        update = { view -> view.data = data },
        modifier = modifier,
    )
}
