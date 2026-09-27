package com.ironlog.app.platform.share;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ShareImageExporter_Factory implements Factory<ShareImageExporter> {
  private final Provider<Context> contextProvider;

  private ShareImageExporter_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ShareImageExporter get() {
    return newInstance(contextProvider.get());
  }

  public static ShareImageExporter_Factory create(Provider<Context> contextProvider) {
    return new ShareImageExporter_Factory(contextProvider);
  }

  public static ShareImageExporter newInstance(Context context) {
    return new ShareImageExporter(context);
  }
}
