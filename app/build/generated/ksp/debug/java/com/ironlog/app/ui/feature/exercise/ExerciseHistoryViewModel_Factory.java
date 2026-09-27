package com.ironlog.app.ui.feature.exercise;

import androidx.lifecycle.SavedStateHandle;
import com.ironlog.app.domain.exercise.ExerciseRepository;
import com.ironlog.app.domain.settings.SettingsRepository;
import com.ironlog.app.domain.workout.WorkoutRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ExerciseHistoryViewModel_Factory implements Factory<ExerciseHistoryViewModel> {
  private final Provider<WorkoutRepository> workoutRepositoryProvider;

  private final Provider<ExerciseRepository> exerciseRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private ExerciseHistoryViewModel_Factory(Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.workoutRepositoryProvider = workoutRepositoryProvider;
    this.exerciseRepositoryProvider = exerciseRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ExerciseHistoryViewModel get() {
    return newInstance(workoutRepositoryProvider.get(), exerciseRepositoryProvider.get(), settingsRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static ExerciseHistoryViewModel_Factory create(
      Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ExerciseHistoryViewModel_Factory(workoutRepositoryProvider, exerciseRepositoryProvider, settingsRepositoryProvider, savedStateHandleProvider);
  }

  public static ExerciseHistoryViewModel newInstance(WorkoutRepository workoutRepository,
      ExerciseRepository exerciseRepository, SettingsRepository settingsRepository,
      SavedStateHandle savedStateHandle) {
    return new ExerciseHistoryViewModel(workoutRepository, exerciseRepository, settingsRepository, savedStateHandle);
  }
}
