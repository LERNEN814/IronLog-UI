package com.ironlog.app.ui.feature.summary

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** M5-T5.3: off-screen rendering produces the fixed 1080-wide card. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ShareCardRendererTest {

    private val sample = ShareCardData(
        date = "2026-09-25",
        durationText = "60:00",
        volumeText = "1440kg",
        exercises = listOf(
            ShareExerciseBest(name = "Bench Press", bestSetText = "60000 x 8", isPr = true),
            ShareExerciseBest(name = "Squat", bestSetText = "100000 x 5", isPr = false),
        ),
        bodyRegions = listOf(0, 4),
        appName = "IronLog",
    )

    @Test
    fun rendersBitmapWithTheExpectedSize() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val bitmap = ShareCardRenderer.render(context, sample)

        assertThat(bitmap.width).isEqualTo(ShareCardView.CARD_WIDTH)
        assertThat(bitmap.height).isEqualTo(ShareCardView.CARD_HEIGHT)
    }

    @Test
    fun rendersWithoutExercisesAndRegions() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val bitmap = ShareCardRenderer.render(
            context,
            sample.copy(exercises = emptyList(), bodyRegions = emptyList()),
        )

        assertThat(bitmap.width).isEqualTo(ShareCardView.CARD_WIDTH)
        assertThat(bitmap.height).isEqualTo(ShareCardView.CARD_HEIGHT)
    }
}
