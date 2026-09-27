package com.ironlog.app.data.timer;

import com.ironlog.app.core.time.Clock;
import com.ironlog.app.core.time.ElapsedClock;
import com.ironlog.app.domain.timer.AppForegroundState;
import com.ironlog.app.domain.timer.RestNotifier;
import com.ironlog.app.domain.workout.WorkoutRepository;
import com.ironlog.app.platform.timer.RestAlarmScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class RestTimerRepository_Factory implements Factory<RestTimerRepository> {
  private final Provider<WorkoutRepository> workoutRepositoryProvider;

  private final Provider<RestAlarmScheduler> schedulerProvider;

  private final Provider<RestNotifier> notifierProvider;

  private final Provider<AppForegroundState> foregroundStateProvider;

  private final Provider<Clock> clockProvider;

  private final Provider<ElapsedClock> elapsedClockProvider;

  private RestTimerRepository_Factory(Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<RestAlarmScheduler> schedulerProvider, Provider<RestNotifier> notifierProvider,
      Provider<AppForegroundState> foregroundStateProvider, Provider<Clock> clockProvider,
      Provider<ElapsedClock> elapsedClockProvider) {
    this.workoutRepositoryProvider = workoutRepositoryProvider;
    this.schedulerProvider = schedulerProvider;
    this.notifierProvider = notifierProvider;
    this.foregroundStateProvider = foregroundStateProvider;
    this.clockProvider = clockProvider;
    this.elapsedClockProvider = elapsedClockProvider;
  }

  @Override
  public RestTimerRepository get() {
    return newInstance(workoutRepositoryProvider.get(), schedulerProvider.get(), notifierProvider.get(), foregroundStateProvider.get(), clockProvider.get(), elapsedClockProvider.get());
  }

  public static RestTimerRepository_Factory create(
      Provider<WorkoutRepository> workoutRepositoryProvider,
      Provider<RestAlarmScheduler> schedulerProvider, Provider<RestNotifier> notifierProvider,
      Provider<AppForegroundState> foregroundStateProvider, Provider<Clock> clockProvider,
      Provider<ElapsedClock> elapsedClockProvider) {
    return new RestTimerRepository_Factory(workoutRepositoryProvider, schedulerProvider, notifierProvider, foregroundStateProvider, clockProvider, elapsedClockProvider);
  }

  public static RestTimerRepository newInstance(WorkoutRepository workoutRepository,
      RestAlarmScheduler scheduler, RestNotifier notifier, AppForegroundState foregroundState,
      Clock clock, ElapsedClock elapsedClock) {
    return new RestTimerRepository(workoutRepository, scheduler, notifier, foregroundState, clock, elapsedClock);
  }
}
