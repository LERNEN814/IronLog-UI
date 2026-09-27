package com.ironlog.app.data.seed;

import android.content.Context;
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
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SeedImporter_Factory implements Factory<SeedImporter> {
  private final Provider<Context> contextProvider;

  private final Provider<MuscleGroupDao> muscleGroupDaoProvider;

  private final Provider<ExerciseDao> exerciseDaoProvider;

  private final Provider<Clock> clockProvider;

  private SeedImporter_Factory(Provider<Context> contextProvider,
      Provider<MuscleGroupDao> muscleGroupDaoProvider, Provider<ExerciseDao> exerciseDaoProvider,
      Provider<Clock> clockProvider) {
    this.contextProvider = contextProvider;
    this.muscleGroupDaoProvider = muscleGroupDaoProvider;
    this.exerciseDaoProvider = exerciseDaoProvider;
    this.clockProvider = clockProvider;
  }

  @Override
  public SeedImporter get() {
    return newInstance(contextProvider.get(), muscleGroupDaoProvider.get(), exerciseDaoProvider.get(), clockProvider.get());
  }

  public static SeedImporter_Factory create(Provider<Context> contextProvider,
      Provider<MuscleGroupDao> muscleGroupDaoProvider, Provider<ExerciseDao> exerciseDaoProvider,
      Provider<Clock> clockProvider) {
    return new SeedImporter_Factory(contextProvider, muscleGroupDaoProvider, exerciseDaoProvider, clockProvider);
  }

  public static SeedImporter newInstance(Context context, MuscleGroupDao muscleGroupDao,
      ExerciseDao exerciseDao, Clock clock) {
    return new SeedImporter(context, muscleGroupDao, exerciseDao, clock);
  }
}
