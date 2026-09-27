package com.ironlog.app;

import com.ironlog.app.domain.timer.RestTimer;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<RestTimer> restTimerProvider;

  private MainActivity_MembersInjector(Provider<RestTimer> restTimerProvider) {
    this.restTimerProvider = restTimerProvider;
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectRestTimer(instance, restTimerProvider.get());
  }

  public static MembersInjector<MainActivity> create(Provider<RestTimer> restTimerProvider) {
    return new MainActivity_MembersInjector(restTimerProvider);
  }

  @InjectedFieldSignature("com.ironlog.app.MainActivity.restTimer")
  public static void injectRestTimer(MainActivity instance, RestTimer restTimer) {
    instance.restTimer = restTimer;
  }
}
