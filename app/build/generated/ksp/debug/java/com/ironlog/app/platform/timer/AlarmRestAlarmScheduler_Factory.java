package com.ironlog.app.platform.timer;

import android.content.Context;
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
public final class AlarmRestAlarmScheduler_Factory implements Factory<AlarmRestAlarmScheduler> {
  private final Provider<Context> contextProvider;

  private AlarmRestAlarmScheduler_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AlarmRestAlarmScheduler get() {
    return newInstance(contextProvider.get());
  }

  public static AlarmRestAlarmScheduler_Factory create(Provider<Context> contextProvider) {
    return new AlarmRestAlarmScheduler_Factory(contextProvider);
  }

  public static AlarmRestAlarmScheduler newInstance(Context context) {
    return new AlarmRestAlarmScheduler(context);
  }
}
