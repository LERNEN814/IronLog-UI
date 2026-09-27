package com.ironlog.app.data.repository;

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
public final class FatigueRepositoryImpl_Factory implements Factory<FatigueRepositoryImpl> {
  private final Provider<WorkoutDao> workoutDaoProvider;

  private final Provider<ExerciseDao> exerciseDaoProvider;

  private final Provider<MuscleGroupDao> muscleGroupDaoProvider;

  private FatigueRepositoryImpl_Factory(Provider<WorkoutDao> workoutDaoProvider,
      Provider<ExerciseDao> exerciseDaoProvider, Provider<MuscleGroupDao> muscleGroupDaoProvider) {
    this.workoutDaoProvider = workoutDaoProvider;
    this.exerciseDaoProvider = exerciseDaoProvider;
    this.muscleGroupDaoProvider = muscleGroupDaoProvider;
  }

  @Override
  public FatigueRepositoryImpl get() {
    return newInstance(workoutDaoProvider.get(), exerciseDaoProvider.get(), muscleGroupDaoProvider.get());
  }

  public static FatigueRepositoryImpl_Factory create(Provider<WorkoutDao> workoutDaoProvider,
      Provider<ExerciseDao> exerciseDaoProvider, Provider<MuscleGroupDao> muscleGroupDaoProvider) {
    return new FatigueRepositoryImpl_Factory(workoutDaoProvider, exerciseDaoProvider, muscleGroupDaoProvider);
  }

  public static FatigueRepositoryImpl newInstance(WorkoutDao workoutDao, ExerciseDao exerciseDao,
      MuscleGroupDao muscleGroupDao) {
    return new FatigueRepositoryImpl(workoutDao, exerciseDao, muscleGroupDao);
  }
}
