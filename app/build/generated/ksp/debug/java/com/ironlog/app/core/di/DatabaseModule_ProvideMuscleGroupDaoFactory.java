package com.ironlog.app.core.di;

import com.ironlog.app.data.db.IronLogDatabase;
import com.ironlog.app.data.db.dao.MuscleGroupDao;
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
public final class DatabaseModule_ProvideMuscleGroupDaoFactory implements Factory<MuscleGroupDao> {
  private final Provider<IronLogDatabase> databaseProvider;

  private DatabaseModule_ProvideMuscleGroupDaoFactory(Provider<IronLogDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public MuscleGroupDao get() {
    return provideMuscleGroupDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideMuscleGroupDaoFactory create(
      Provider<IronLogDatabase> databaseProvider) {
    return new DatabaseModule_ProvideMuscleGroupDaoFactory(databaseProvider);
  }

  public static MuscleGroupDao provideMuscleGroupDao(IronLogDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMuscleGroupDao(database));
  }
}
