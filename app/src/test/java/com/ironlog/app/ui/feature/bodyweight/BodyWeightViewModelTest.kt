package com.ironlog.app.ui.feature.bodyweight

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.time.FixedClock
import com.ironlog.app.domain.bodyweight.BodyWeightRepository
import com.ironlog.app.domain.model.BodyWeight
import com.ironlog.app.testutil.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BodyWeightViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun savingSameDayReplacesExistingEntry() = runTest(dispatcher) {
        val repository = FakeBodyWeightRepository()
        val viewModel = BodyWeightViewModel(repository, FakeSettingsRepository(), FixedClock(1_700_000_000_000))
        viewModel.save(72_500)
        testScheduler.advanceUntilIdle()
        viewModel.save(73_000)
        testScheduler.advanceUntilIdle()
        assertThat(repository.values).containsExactly("2023-11-15", 73_000)
    }
}

private class FakeBodyWeightRepository : BodyWeightRepository {
    val values = linkedMapOf<String, Int>()
    override suspend fun upsert(localDate: String, weightGrams: Int) { values[localDate] = weightGrams }
    override suspend fun recent(limit: Int): List<BodyWeight> = values.entries.mapIndexed { index, (date, grams) -> BodyWeight(index.toString(), date, grams, 0, 0, 0) }.reversed()
    override suspend fun delete(localDate: String) { values.remove(localDate) }
}
