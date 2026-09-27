package com.ironlog.app.core.di;

import com.ironlog.app.data.db.IronLogDatabase;
import com.ironlog.app.data.db.dao.BodyWeightDao;
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
public final class DatabaseModule_ProvideBodyWeightDaoFactory implements Factory<BodyWeightDao> {
  private final Provider<IronLogDatabase> databaseProvider;

  private DatabaseModule_ProvideBodyWeightDaoFactory(Provider<IronLogDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public BodyWeightDao get() {
    return provideBodyWeightDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideBodyWeightDaoFactory create(
      Provider<IronLogDatabase> databaseProvider) {
    return new DatabaseModule_ProvideBodyWeightDaoFactory(databaseProvider);
  }

  public static BodyWeightDao provideBodyWeightDao(IronLogDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBodyWeightDao(database));
  }
}
