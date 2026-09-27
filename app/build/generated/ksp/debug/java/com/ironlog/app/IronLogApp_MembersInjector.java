package com.ironlog.app;

import com.ironlog.app.core.di.ApplicationScope;
import com.ironlog.app.data.seed.SeedImporter;
import com.ironlog.app.platform.timer.ForegroundTimerBridge;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineScope;

@QualifierMetadata("com.ironlog.app.core.di.ApplicationScope")
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
public final class IronLogApp_MembersInjector implements MembersInjector<IronLogApp> {
  private final Provider<SeedImporter> seedImporterProvider;

  private final Provider<ForegroundTimerBridge> foregroundTimerBridgeProvider;

  private final Provider<CoroutineScope> applicationScopeProvider;

  private IronLogApp_MembersInjector(Provider<SeedImporter> seedImporterProvider,
      Provider<ForegroundTimerBridge> foregroundTimerBridgeProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    this.seedImporterProvider = seedImporterProvider;
    this.foregroundTimerBridgeProvider = foregroundTimerBridgeProvider;
    this.applicationScopeProvider = applicationScopeProvider;
  }

  @Override
  public void injectMembers(IronLogApp instance) {
    injectSeedImporter(instance, seedImporterProvider.get());
    injectForegroundTimerBridge(instance, foregroundTimerBridgeProvider.get());
    injectApplicationScope(instance, applicationScopeProvider.get());
  }

  public static MembersInjector<IronLogApp> create(Provider<SeedImporter> seedImporterProvider,
      Provider<ForegroundTimerBridge> foregroundTimerBridgeProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    return new IronLogApp_MembersInjector(seedImporterProvider, foregroundTimerBridgeProvider, applicationScopeProvider);
  }

  @InjectedFieldSignature("com.ironlog.app.IronLogApp.seedImporter")
  public static void injectSeedImporter(IronLogApp instance, SeedImporter seedImporter) {
    instance.seedImporter = seedImporter;
  }

  @InjectedFieldSignature("com.ironlog.app.IronLogApp.foregroundTimerBridge")
  public static void injectForegroundTimerBridge(IronLogApp instance,
      ForegroundTimerBridge foregroundTimerBridge) {
    instance.foregroundTimerBridge = foregroundTimerBridge;
  }

  @InjectedFieldSignature("com.ironlog.app.IronLogApp.applicationScope")
  @ApplicationScope
  public static void injectApplicationScope(IronLogApp instance, CoroutineScope applicationScope) {
    instance.applicationScope = applicationScope;
  }
}
