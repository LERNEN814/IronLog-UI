package com.ironlog.app.ui.feature.history;

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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<WorkoutRepository> workoutRepositoryProvider;

  private HistoryViewModel_Factory(Provider<WorkoutRepository> workoutRepositoryProvider) {
    this.workoutRepositoryProvider = workoutRepositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(workoutRepositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(
      Provider<WorkoutRepository> workoutRepositoryProvider) {
    return new HistoryViewModel_Factory(workoutRepositoryProvider);
  }

  public static HistoryViewModel newInstance(WorkoutRepository workoutRepository) {
    return new HistoryViewModel(workoutRepository);
  }
}
