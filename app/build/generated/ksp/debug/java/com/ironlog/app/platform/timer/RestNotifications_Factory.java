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
public final class RestNotifications_Factory implements Factory<RestNotifications> {
  private final Provider<Context> contextProvider;

  private RestNotifications_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public RestNotifications get() {
    return newInstance(contextProvider.get());
  }

  public static RestNotifications_Factory create(Provider<Context> contextProvider) {
    return new RestNotifications_Factory(contextProvider);
  }

  public static RestNotifications newInstance(Context context) {
    return new RestNotifications(context);
  }
}
