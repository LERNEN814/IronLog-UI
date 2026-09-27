package com.ironlog.app.ui.feature.history;

import androidx.lifecycle.SavedStateHandle;
import com.ironlog.app.core.time.Clock;
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
public final class SessionDetailViewModel_Factory implements Factory<SessionDetailViewModel> {
  private final Provider<WorkoutRepository> workoutRepositoryProvider;

  private final Provider<ExerciseRepository> exerciseRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<Clock> clockProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private SessionDetailViewModel_Factory(Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<Clock> clockProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.workoutRepositoryProvider = workoutRepositoryProvider;
    this.exerciseRepositoryProvider = exerciseRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.clockProvider = clockProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public SessionDetailViewModel get() {
    return newInstance(workoutRepositoryProvider.get(), exerciseRepositoryProvider.get(), settingsRepositoryProvider.get(), clockProvider.get(), savedStateHandleProvider.get());
  }

  public static SessionDetailViewModel_Factory create(
      Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<Clock> clockProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new SessionDetailViewModel_Factory(workoutRepositoryProvider, exerciseRepositoryProvider, settingsRepositoryProvider, clockProvider, savedStateHandleProvider);
  }

  public static SessionDetailViewModel newInstance(WorkoutRepository workoutRepository,
      ExerciseRepository exerciseRepository, SettingsRepository settingsRepository, Clock clock,
      SavedStateHandle savedStateHandle) {
    return new SessionDetailViewModel(workoutRepository, exerciseRepository, settingsRepository, clock, savedStateHandle);
  }
}
