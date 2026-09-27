package com.ironlog.app.data.repository;

import com.ironlog.app.core.id.IdGenerator;
import com.ironlog.app.core.time.Clock;
import com.ironlog.app.data.db.dao.ExerciseDao;
import com.ironlog.app.data.db.dao.MuscleGroupDao;
import com.ironlog.app.data.db.dao.WorkoutDao;
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
public final class WorkoutRepositoryImpl_Factory implements Factory<WorkoutRepositoryImpl> {
  private final Provider<WorkoutDao> workoutDaoProvider;

  private final Provider<ExerciseDao> exerciseDaoProvider;

  private final Provider<MuscleGroupDao> muscleGroupDaoProvider;

  private final Provider<Clock> clockProvider;

  private final Provider<IdGenerator> idGeneratorProvider;

  private WorkoutRepositoryImpl_Factory(Provider<WorkoutDao> workoutDaoProvider,
      Provider<ExerciseDao> exerciseDaoProvider, Provider<MuscleGroupDao> muscleGroupDaoProvider,
      Provider<Clock> clockProvider, Provider<IdGenerator> idGeneratorProvider) {
    this.workoutDaoProvider = workoutDaoProvider;
    this.exerciseDaoProvider = exerciseDaoProvider;
    this.muscleGroupDaoProvider = muscleGroupDaoProvider;
    this.clockProvider = clockProvider;
    this.idGeneratorProvider = idGeneratorProvider;
  }

  @Override
  public WorkoutRepositoryImpl get() {
    return newInstance(workoutDaoProvider.get(), exerciseDaoProvider.get(), muscleGroupDaoProvider.get(), clockProvider.get(), idGeneratorProvider.get());
  }

  public static WorkoutRepositoryImpl_Factory create(Provider<WorkoutDao> workoutDaoProvider,
      Provider<ExerciseDao> exerciseDaoProvider, Provider<MuscleGroupDao> muscleGroupDaoProvider,
      Provider<Clock> clockProvider, Provider<IdGenerator> idGeneratorProvider) {
    return new WorkoutRepositoryImpl_Factory(workoutDaoProvider, exerciseDaoProvider, muscleGroupDaoProvider, clockProvider, idGeneratorProvider);
  }

  public static WorkoutRepositoryImpl newInstance(WorkoutDao workoutDao, ExerciseDao exerciseDao,
      MuscleGroupDao muscleGroupDao, Clock clock, IdGenerator idGenerator) {
    return new WorkoutRepositoryImpl(workoutDao, exerciseDao, muscleGroupDao, clock, idGenerator);
  }
}
