package com.ironlog.app.ui.feature.home;

import com.ironlog.app.core.time.Clock;
import com.ironlog.app.domain.exercise.ExerciseRepository;
import com.ironlog.app.domain.fatigue.FatigueRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<WorkoutRepository> workoutRepositoryProvider;

  private final Provider<FatigueRepository> fatigueRepositoryProvider;

  private final Provider<ExerciseRepository> exerciseRepositoryProvider;

  private final Provider<Clock> clockProvider;

  private HomeViewModel_Factory(Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<FatigueRepository> fatigueRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider, Provider<Clock> clockProvider) {
    this.workoutRepositoryProvider = workoutRepositoryProvider;
    this.fatigueRepositoryProvider = fatigueRepositoryProvider;
    this.exerciseRepositoryProvider = exerciseRepositoryProvider;
    this.clockProvider = clockProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(workoutRepositoryProvider.get(), fatigueRepositoryProvider.get(), exerciseRepositoryProvider.get(), clockProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<FatigueRepository> fatigueRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider, Provider<Clock> clockProvider) {
    return new HomeViewModel_Factory(workoutRepositoryProvider, fatigueRepositoryProvider, exerciseRepositoryProvider, clockProvider);
  }

  public static HomeViewModel newInstance(WorkoutRepository workoutRepository,
      FatigueRepository fatigueRepository, ExerciseRepository exerciseRepository, Clock clock) {
    return new HomeViewModel(workoutRepository, fatigueRepository, exerciseRepository, clock);
  }
}
