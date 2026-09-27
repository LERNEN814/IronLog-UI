package com.ironlog.app.platform.timer;

import com.ironlog.app.domain.timer.RestTimer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
public final class ForegroundTimerBridge_Factory implements Factory<ForegroundTimerBridge> {
  private final Provider<RestTimer> restTimerProvider;

  private ForegroundTimerBridge_Factory(Provider<RestTimer> restTimerProvider) {
    this.restTimerProvider = restTimerProvider;
  }

  @Override
  public ForegroundTimerBridge get() {
    return newInstance(restTimerProvider.get());
  }

  public static ForegroundTimerBridge_Factory create(Provider<RestTimer> restTimerProvider) {
    return new ForegroundTimerBridge_Factory(restTimerProvider);
  }

  public static ForegroundTimerBridge newInstance(RestTimer restTimer) {
    return new ForegroundTimerBridge(restTimer);
  }
}
