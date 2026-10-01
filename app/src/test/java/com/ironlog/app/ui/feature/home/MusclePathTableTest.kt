package com.ironlog.app.ui.feature.home

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MusclePathTableTest {
    @Test fun hasBothViewsAndStableCanonicalIds() {
        assertThat(MusclePathTable.forView(BodyView.FRONT)).isNotEmpty()
        assertThat(MusclePathTable.forView(BodyView.BACK)).isNotEmpty()
        assertThat(MusclePathTable.regions.map { it.sourceId }).containsNoDuplicates()
        assertThat(MusclePathTable.regions.map { it.canonicalMuscleId }).doesNotContain("lats")
        assertThat(MusclePathTable.regions.map { it.canonicalMuscleId }).doesNotContain("rear_delts")
    }

    @Test fun pathDataIsClosedAndNonEmpty() {
        MusclePathTable.regions.forEach { region ->
            assertThat(region.pathData).contains("M ")
            assertThat(region.pathData).contains("Z")
            assertThat(region.pathData.length).isGreaterThan(12)
        }
    }

    @Test fun invalidPathFallsBackWithoutThrowing() {
        val invalid = PathRegion("bad", "chest", RegionSide.CENTER, BodyView.FRONT, "broken", 0)
        assertThat(PathGeometry.parse(invalid)).isNull()
        assertThat(PathGeometry.hitTest(invalid, androidx.compose.ui.geometry.Offset(10f, 10f), 360f, 720f)).isFalse()
    }

    @Test fun unresolvedRegionsAreAbsentFromInteractiveTable() {
        assertThat(MusclePathTable.regions.filter { it.interactive }.map { it.canonicalMuscleId })
            .containsNoneOf("lats", "rear_delts", "head")
    }
}
