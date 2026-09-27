package com.ironlog.app.core.di;

import com.ironlog.app.data.db.IronLogDatabase;
import com.ironlog.app.data.db.dao.ExerciseDao;
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
public final class DatabaseModule_ProvideExerciseDaoFactory implements Factory<ExerciseDao> {
  private final Provider<IronLogDatabase> databaseProvider;

  private DatabaseModule_ProvideExerciseDaoFactory(Provider<IronLogDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ExerciseDao get() {
    return provideExerciseDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideExerciseDaoFactory create(
      Provider<IronLogDatabase> databaseProvider) {
    return new DatabaseModule_ProvideExerciseDaoFactory(databaseProvider);
  }

  public static ExerciseDao provideExerciseDao(IronLogDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideExerciseDao(database));
  }
}
