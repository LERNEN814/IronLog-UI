package com.ironlog.app.core.di;

import com.ironlog.app.core.id.IdGenerator;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_Companion_ProvideIdGeneratorFactory implements Factory<IdGenerator> {
  @Override
  public IdGenerator get() {
    return provideIdGenerator();
  }

  public static AppModule_Companion_ProvideIdGeneratorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static IdGenerator provideIdGenerator() {
    return Preconditions.checkNotNullFromProvides(AppModule.Companion.provideIdGenerator());
  }

  private static final class InstanceHolder {
    static final AppModule_Companion_ProvideIdGeneratorFactory INSTANCE = new AppModule_Companion_ProvideIdGeneratorFactory();
  }
}
