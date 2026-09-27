package com.ironlog.app.ui.feature.exercise

import com.google.common.truth.Truth.assertThat
import com.ironlog.app.domain.model.Exercise
import com.ironlog.app.domain.model.ExerciseKind
import com.ironlog.app.domain.model.MuscleGroup
import com.ironlog.app.testutil.FakeExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExercisePickerViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun muscleGroup(id: String, bodyRegion: Int, sortOrder: Int) = MuscleGroup(
        id = id,
        displayNameEn = id,
        displayNameZh = id,
        bodyRegion = bodyRegion,
        recoveryHalfLifeHours = 48,
        sortOrder = sortOrder,
    )

    private fun exercise(
        id: String,
        nameZh: String,
        nameEn: String,
        primaryMuscleId: String?,
        isCustom: Boolean = false,
    ) = Exercise(
        id = id,
        nameZh = nameZh,
        nameEn = nameEn,
        kind = ExerciseKind.STRENGTH,
        equipment = "barbell",
        primaryMuscleId = primaryMuscleId,
        isCustom = isCustom,
        isArchived = false,
        createdAt = 0,
        updatedAt = 0,
    )

    @Test
    fun searchFiltersByChineseAndEnglishNameIgnoringCase() = runTest(dispatcher) {
        val repository = FakeExerciseRepository()
        repository.exercises.value = listOf(
            exercise("bench", "杠铃卧推", "Barbell Bench Press", "chest"),
            exercise("squat", "深蹲", "Back Squat", "quads"),
        )
        val viewModel = ExercisePickerViewModel(repository)
        viewModel.uiState.first { !it.loading }

        viewModel.onQueryChange("深蹲")
        val chinese = viewModel.uiState.first { it.query == "深蹲" && it.exercises.size == 1 }
        assertThat(chinese.exercises.single().id).isEqualTo("squat")

        viewModel.onQueryChange("SQUAT")
        val english = viewModel.uiState.first { it.query == "SQUAT" && it.exercises.size == 1 }
        assertThat(english.exercises.single().id).isEqualTo("squat")
    }

    @Test
    fun regionFilterKeepsOnlyThatBodyRegion() = runTest(dispatcher) {
        val repository = FakeExerciseRepository()
        repository.muscleGroups.value = listOf(
            muscleGroup("chest", 0, 0),
            muscleGroup("quads", 4, 1),
        )
        repository.exercises.value = listOf(
            exercise("bench", "杠铃卧推", "Barbell Bench Press", "chest"),
            exercise("squat", "深蹲", "Back Squat", "quads"),
        )
        val viewModel = ExercisePickerViewModel(repository)
        viewModel.uiState.first { !it.loading }

        viewModel.onRegionSelected(4)

        val state = viewModel.uiState.first { it.bodyRegion == 4 && it.exercises.size == 1 }
        assertThat(state.exercises.single().id).isEqualTo("squat")
    }

    @Test
    fun createdCustomExerciseIsFirstInListAndSelected() = runTest(dispatcher) {
        val repository = FakeExerciseRepository()
        repository.muscleGroups.value = listOf(
            muscleGroup("chest", 0, 0),
            muscleGroup("biceps", 3, 1),
        )
        repository.exercises.value = listOf(
            exercise("bench", "杠铃卧推", "Barbell Bench Press", "chest"),
        )
        val viewModel = ExercisePickerViewModel(repository)
        viewModel.uiState.first { !it.loading }

        viewModel.onSaveCustom(
            nameZh = "哑铃弯举",
            nameEn = "Dumbbell Curl",
            kind = ExerciseKind.STRENGTH,
            equipment = "dumbbell",
            primaryMuscleId = "biceps",
            secondaryMuscleIds = emptyList(),
        )

        val event = viewModel.events.first()
        assertThat(event).isInstanceOf(ExercisePickerEvent.ExerciseSelected::class.java)

        val state = viewModel.uiState.first { it.exercises.any { exercise -> exercise.isCustom } }
        assertThat(state.exercises.first().isCustom).isTrue()
        assertThat(state.exercises.first().nameZh).isEqualTo("哑铃弯举")
        assertThat(state.formVisible).isFalse()
    }

    @Test
    fun recentlyUsedExercisesSortBeforeUnusedOnes() = runTest(dispatcher) {
        val repository = FakeExerciseRepository()
        repository.exercises.value = listOf(
            exercise("bench", "A Bench", "A Bench", "chest"),
            exercise("squat", "B Squat", "B Squat", "quads"),
        )
        repository.lastUsed.value = mapOf("squat" to 1_000L)
        val viewModel = ExercisePickerViewModel(repository)

        val state = viewModel.uiState.first { !it.loading }

        assertThat(state.exercises.first().id).isEqualTo("squat")
    }
}
