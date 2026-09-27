package com.ironlog.app.platform.timer;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ProcessForegroundState_Factory implements Factory<ProcessForegroundState> {
  @Override
  public ProcessForegroundState get() {
    return newInstance();
  }

  public static ProcessForegroundState_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ProcessForegroundState newInstance() {
    return new ProcessForegroundState();
  }

  private static final class InstanceHolder {
    static final ProcessForegroundState_Factory INSTANCE = new ProcessForegroundState_Factory();
  }
}
