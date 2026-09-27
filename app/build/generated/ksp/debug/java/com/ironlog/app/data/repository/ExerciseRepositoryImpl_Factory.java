package com.ironlog.app.data.repository;

import com.ironlog.app.core.id.IdGenerator;
import com.ironlog.app.core.time.Clock;
import com.ironlog.app.data.db.dao.ExerciseDao;
import com.ironlog.app.data.db.dao.MuscleGroupDao;
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
public final class ExerciseRepositoryImpl_Factory implements Factory<ExerciseRepositoryImpl> {
  private final Provider<ExerciseDao> exerciseDaoProvider;

  private final Provider<MuscleGroupDao> muscleGroupDaoProvider;

  private final Provider<Clock> clockProvider;

  private final Provider<IdGenerator> idGeneratorProvider;

  private ExerciseRepositoryImpl_Factory(Provider<ExerciseDao> exerciseDaoProvider,
      Provider<MuscleGroupDao> muscleGroupDaoProvider, Provider<Clock> clockProvider,
      Provider<IdGenerator> idGeneratorProvider) {
    this.exerciseDaoProvider = exerciseDaoProvider;
    this.muscleGroupDaoProvider = muscleGroupDaoProvider;
    this.clockProvider = clockProvider;
    this.idGeneratorProvider = idGeneratorProvider;
  }

  @Override
  public ExerciseRepositoryImpl get() {
    return newInstance(exerciseDaoProvider.get(), muscleGroupDaoProvider.get(), clockProvider.get(), idGeneratorProvider.get());
  }

  public static ExerciseRepositoryImpl_Factory create(Provider<ExerciseDao> exerciseDaoProvider,
      Provider<MuscleGroupDao> muscleGroupDaoProvider, Provider<Clock> clockProvider,
      Provider<IdGenerator> idGeneratorProvider) {
    return new ExerciseRepositoryImpl_Factory(exerciseDaoProvider, muscleGroupDaoProvider, clockProvider, idGeneratorProvider);
  }

  public static ExerciseRepositoryImpl newInstance(ExerciseDao exerciseDao,
      MuscleGroupDao muscleGroupDao, Clock clock, IdGenerator idGenerator) {
    return new ExerciseRepositoryImpl(exerciseDao, muscleGroupDao, clock, idGenerator);
  }
}
