package com.ironlog.app.core.di;

import com.ironlog.app.core.time.ElapsedClock;
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
public final class AppModule_Companion_ProvideElapsedClockFactory implements Factory<ElapsedClock> {
  @Override
  public ElapsedClock get() {
    return provideElapsedClock();
  }

  public static AppModule_Companion_ProvideElapsedClockFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ElapsedClock provideElapsedClock() {
    return Preconditions.checkNotNullFromProvides(AppModule.Companion.provideElapsedClock());
  }

  private static final class InstanceHolder {
    static final AppModule_Companion_ProvideElapsedClockFactory INSTANCE = new AppModule_Companion_ProvideElapsedClockFactory();
  }
}
