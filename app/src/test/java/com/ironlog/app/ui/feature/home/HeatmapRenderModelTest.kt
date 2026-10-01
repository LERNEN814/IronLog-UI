package com.ironlog.app.ui.feature.home

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HeatmapRenderModelTest {
    @Test fun preservesViewSelectionAndScores() {
        val model = HeatmapRenderModel(BodyView.BACK, mapOf("lats" to 72), "lats")
        assertThat(model.view).isEqualTo(BodyView.BACK)
        assertThat(model.scores["lats"]).isEqualTo(72)
        assertThat(model.selectedMuscleId).isEqualTo("lats")
    }

    @Test fun viewSelectionClearsMuscleNotVisibleOnTargetView() {
        val selected = "chest"
        val back = MusclePathTable.forView(BodyView.BACK)
        assertThat(back.any { it.interactive && it.canonicalMuscleId == selected }).isFalse()
    }
}
