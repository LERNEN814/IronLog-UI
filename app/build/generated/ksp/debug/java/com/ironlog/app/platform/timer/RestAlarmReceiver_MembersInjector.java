package com.ironlog.app.platform.timer;

import com.ironlog.app.data.timer.RestTimerRepository;
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
public final class RestAlarmReceiver_MembersInjector implements MembersInjector<RestAlarmReceiver> {
  private final Provider<RestTimerRepository> restTimerRepositoryProvider;

  private RestAlarmReceiver_MembersInjector(
      Provider<RestTimerRepository> restTimerRepositoryProvider) {
    this.restTimerRepositoryProvider = restTimerRepositoryProvider;
  }

  @Override
  public void injectMembers(RestAlarmReceiver instance) {
    injectRestTimerRepository(instance, restTimerRepositoryProvider.get());
  }

  public static MembersInjector<RestAlarmReceiver> create(
      Provider<RestTimerRepository> restTimerRepositoryProvider) {
    return new RestAlarmReceiver_MembersInjector(restTimerRepositoryProvider);
  }

  @InjectedFieldSignature("com.ironlog.app.platform.timer.RestAlarmReceiver.restTimerRepository")
  public static void injectRestTimerRepository(RestAlarmReceiver instance,
      RestTimerRepository restTimerRepository) {
    instance.restTimerRepository = restTimerRepository;
  }
}
