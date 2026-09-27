package com.ironlog.app.ui.feature.session;

import androidx.lifecycle.SavedStateHandle;
import com.ironlog.app.core.time.Clock;
import com.ironlog.app.domain.exercise.ExerciseRepository;
import com.ironlog.app.domain.settings.SettingsRepository;
import com.ironlog.app.domain.timer.RestTimer;
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
public final class SessionViewModel_Factory implements Factory<SessionViewModel> {
  private final Provider<WorkoutRepository> workoutRepositoryProvider;

  private final Provider<ExerciseRepository> exerciseRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<RestTimer> restTimerProvider;

  private final Provider<Clock> clockProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private SessionViewModel_Factory(Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<RestTimer> restTimerProvider, Provider<Clock> clockProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.workoutRepositoryProvider = workoutRepositoryProvider;
    this.exerciseRepositoryProvider = exerciseRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.restTimerProvider = restTimerProvider;
    this.clockProvider = clockProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public SessionViewModel get() {
    return newInstance(workoutRepositoryProvider.get(), exerciseRepositoryProvider.get(), settingsRepositoryProvider.get(), restTimerProvider.get(), clockProvider.get(), savedStateHandleProvider.get());
  }

  public static SessionViewModel_Factory create(
      Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<RestTimer> restTimerProvider, Provider<Clock> clockProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new SessionViewModel_Factory(workoutRepositoryProvider, exerciseRepositoryProvider, settingsRepositoryProvider, restTimerProvider, clockProvider, savedStateHandleProvider);
  }

  public static SessionViewModel newInstance(WorkoutRepository workoutRepository,
      ExerciseRepository exerciseRepository, SettingsRepository settingsRepository,
      RestTimer restTimer, Clock clock, SavedStateHandle savedStateHandle) {
    return new SessionViewModel(workoutRepository, exerciseRepository, settingsRepository, restTimer, clock, savedStateHandle);
  }
}
