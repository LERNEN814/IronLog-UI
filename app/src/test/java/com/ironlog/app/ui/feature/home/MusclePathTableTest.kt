package com.ironlog.app.ui.feature.home

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MusclePathTableTest {
    @Test fun hasBothViewsAndStableCanonicalIds() {
        assertThat(MusclePathTable.forView(BodyView.FRONT)).isNotEmpty()
        assertThat(MusclePathTable.forView(BodyView.BACK)).isNotEmpty()
        assertThat(MusclePathTable.regions.map { it.sourceId }).containsNoDuplicates()
        assertThat(MusclePathTable.regions.map { it.canonicalMuscleId }).contains("lats")
        assertThat(MusclePathTable.regions.map { it.canonicalMuscleId }).contains("rear_delts")
    }

    @Test fun pathDataIsClosedAndNonEmpty() {
        MusclePathTable.regions.forEach { region ->
            assertThat(region.pathData).contains("M ")
            assertThat(region.pathData).contains("Z")
            assertThat(region.pathData.length).isGreaterThan(12)
        }
    }
}
