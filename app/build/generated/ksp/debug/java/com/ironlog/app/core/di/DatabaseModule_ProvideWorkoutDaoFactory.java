package com.ironlog.app.core.di;

import com.ironlog.app.data.db.IronLogDatabase;
import com.ironlog.app.data.db.dao.WorkoutDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideWorkoutDaoFactory implements Factory<WorkoutDao> {
  private final Provider<IronLogDatabase> databaseProvider;

  private DatabaseModule_ProvideWorkoutDaoFactory(Provider<IronLogDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public WorkoutDao get() {
    return provideWorkoutDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideWorkoutDaoFactory create(
      Provider<IronLogDatabase> databaseProvider) {
    return new DatabaseModule_ProvideWorkoutDaoFactory(databaseProvider);
  }

  public static WorkoutDao provideWorkoutDao(IronLogDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWorkoutDao(database));
  }
}
