package com.ironlog.app.ui.feature.calendar;

import com.ironlog.app.core.time.Clock;
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
public final class CalendarViewModel_Factory implements Factory<CalendarViewModel> {
  private final Provider<WorkoutRepository> workoutRepositoryProvider;

  private final Provider<Clock> clockProvider;

  private CalendarViewModel_Factory(Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<Clock> clockProvider) {
    this.workoutRepositoryProvider = workoutRepositoryProvider;
    this.clockProvider = clockProvider;
  }

  @Override
  public CalendarViewModel get() {
    return newInstance(workoutRepositoryProvider.get(), clockProvider.get());
  }

  public static CalendarViewModel_Factory create(
      Provider<WorkoutRepository> workoutRepositoryProvider, Provider<Clock> clockProvider) {
    return new CalendarViewModel_Factory(workoutRepositoryProvider, clockProvider);
  }

  public static CalendarViewModel newInstance(WorkoutRepository workoutRepository, Clock clock) {
    return new CalendarViewModel(workoutRepository, clock);
  }
}
