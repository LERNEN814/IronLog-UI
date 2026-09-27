package com.ironlog.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.ironlog.app.core.di.AppModule_Companion_ProvideApplicationScopeFactory;
import com.ironlog.app.core.di.AppModule_Companion_ProvideClockFactory;
import com.ironlog.app.core.di.AppModule_Companion_ProvideDataStoreFactory;
import com.ironlog.app.core.di.AppModule_Companion_ProvideElapsedClockFactory;
import com.ironlog.app.core.di.AppModule_Companion_ProvideIdGeneratorFactory;
import com.ironlog.app.core.di.DatabaseModule_ProvideDatabaseFactory;
import com.ironlog.app.core.di.DatabaseModule_ProvideExerciseDaoFactory;
import com.ironlog.app.core.di.DatabaseModule_ProvideMuscleGroupDaoFactory;
import com.ironlog.app.core.di.DatabaseModule_ProvideWorkoutDaoFactory;
import com.ironlog.app.core.id.IdGenerator;
import com.ironlog.app.core.time.Clock;
import com.ironlog.app.core.time.ElapsedClock;
import com.ironlog.app.data.db.IronLogDatabase;
import com.ironlog.app.data.db.dao.ExerciseDao;
import com.ironlog.app.data.db.dao.MuscleGroupDao;
import com.ironlog.app.data.db.dao.WorkoutDao;
import com.ironlog.app.data.repository.ExerciseRepositoryImpl;
import com.ironlog.app.data.repository.FatigueRepositoryImpl;
import com.ironlog.app.data.repository.WorkoutRepositoryImpl;
import com.ironlog.app.data.seed.SeedImporter;
import com.ironlog.app.data.settings.SettingsDataStore;
import com.ironlog.app.data.timer.RestTimerRepository;
import com.ironlog.app.domain.exercise.ExerciseRepository;
import com.ironlog.app.domain.fatigue.FatigueRepository;
import com.ironlog.app.domain.settings.SettingsRepository;
import com.ironlog.app.domain.timer.RestNotifier;
import com.ironlog.app.domain.workout.WorkoutRepository;
import com.ironlog.app.platform.share.ShareImageExporter;
import com.ironlog.app.platform.timer.AlarmRestAlarmScheduler;
import com.ironlog.app.platform.timer.ForegroundTimerBridge;
import com.ironlog.app.platform.timer.ProcessForegroundState;
import com.ironlog.app.platform.timer.RestAlarmReceiver;
import com.ironlog.app.platform.timer.RestAlarmReceiver_MembersInjector;
import com.ironlog.app.platform.timer.RestAlarmScheduler;
import com.ironlog.app.platform.timer.RestNotifications;
import com.ironlog.app.ui.MainViewModel;
import com.ironlog.app.ui.MainViewModel_HiltModules;
import com.ironlog.app.ui.MainViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ironlog.app.ui.MainViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ironlog.app.ui.feature.calendar.CalendarViewModel;
import com.ironlog.app.ui.feature.calendar.CalendarViewModel_HiltModules;
import com.ironlog.app.ui.feature.calendar.CalendarViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ironlog.app.ui.feature.calendar.CalendarViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ironlog.app.ui.feature.exercise.ExerciseHistoryViewModel;
import com.ironlog.app.ui.feature.exercise.ExerciseHistoryViewModel_HiltModules;
import com.ironlog.app.ui.feature.exercise.ExerciseHistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ironlog.app.ui.feature.exercise.ExerciseHistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ironlog.app.ui.feature.exercise.ExercisePickerViewModel;
import com.ironlog.app.ui.feature.exercise.ExercisePickerViewModel_HiltModules;
import com.ironlog.app.ui.feature.exercise.ExercisePickerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ironlog.app.ui.feature.exercise.ExercisePickerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ironlog.app.ui.feature.history.HistoryViewModel;
import com.ironlog.app.ui.feature.history.HistoryViewModel_HiltModules;
import com.ironlog.app.ui.feature.history.HistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ironlog.app.ui.feature.history.HistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ironlog.app.ui.feature.history.SessionDetailViewModel;
import com.ironlog.app.ui.feature.history.SessionDetailViewModel_HiltModules;
import com.ironlog.app.ui.feature.history.SessionDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ironlog.app.ui.feature.history.SessionDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ironlog.app.ui.feature.home.HomeViewModel;
import com.ironlog.app.ui.feature.home.HomeViewModel_HiltModules;
import com.ironlog.app.ui.feature.home.HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ironlog.app.ui.feature.home.HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ironlog.app.ui.feature.session.SessionViewModel;
import com.ironlog.app.ui.feature.session.SessionViewModel_HiltModules;
import com.ironlog.app.ui.feature.session.SessionViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ironlog.app.ui.feature.session.SessionViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ironlog.app.ui.feature.settings.SettingsViewModel;
import com.ironlog.app.ui.feature.settings.SettingsViewModel_HiltModules;
import com.ironlog.app.ui.feature.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ironlog.app.ui.feature.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineScope;

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
public final class DaggerIronLogApp_HiltComponents_SingletonC {
  private DaggerIronLogApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public IronLogApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements IronLogApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public IronLogApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements IronLogApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public IronLogApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements IronLogApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public IronLogApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements IronLogApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public IronLogApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements IronLogApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public IronLogApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements IronLogApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public IronLogApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements IronLogApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public IronLogApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends IronLogApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends IronLogApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends IronLogApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends IronLogApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    Map keySetMapOfClassOfObjectAndBooleanBuilder() {
      MapBuilder mapBuilder = MapBuilder.<String, Boolean>newMapBuilder(9);
      mapBuilder.put(CalendarViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CalendarViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(ExerciseHistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ExerciseHistoryViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(ExercisePickerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ExercisePickerViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(HistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HistoryViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HomeViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(MainViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, MainViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(SessionDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SessionDetailViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(SessionViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SessionViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide());
      return mapBuilder.build();
    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(keySetMapOfClassOfObjectAndBooleanBuilder());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectRestTimer(instance, singletonCImpl.restTimerRepositoryProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends IronLogApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<CalendarViewModel> calendarViewModelProvider;

    Provider<ExerciseHistoryViewModel> exerciseHistoryViewModelProvider;

    Provider<ExercisePickerViewModel> exercisePickerViewModelProvider;

    Provider<HistoryViewModel> historyViewModelProvider;

    Provider<HomeViewModel> homeViewModelProvider;

    Provider<MainViewModel> mainViewModelProvider;

    Provider<SessionDetailViewModel> sessionDetailViewModelProvider;

    Provider<SessionViewModel> sessionViewModelProvider;

    Provider<SettingsViewModel> settingsViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    Map hiltViewModelMapMapOfClassOfObjectAndProviderOfViewModelBuilder() {
      MapBuilder mapBuilder = MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(9);
      mapBuilder.put(CalendarViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (calendarViewModelProvider)));
      mapBuilder.put(ExerciseHistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (exerciseHistoryViewModelProvider)));
      mapBuilder.put(ExercisePickerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (exercisePickerViewModelProvider)));
      mapBuilder.put(HistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (historyViewModelProvider)));
      mapBuilder.put(HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (homeViewModelProvider)));
      mapBuilder.put(MainViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (mainViewModelProvider)));
      mapBuilder.put(SessionDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (sessionDetailViewModelProvider)));
      mapBuilder.put(SessionViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (sessionViewModelProvider)));
      mapBuilder.put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (settingsViewModelProvider)));
      return mapBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.calendarViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.exerciseHistoryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.exercisePickerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.mainViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.sessionDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.sessionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(hiltViewModelMapMapOfClassOfObjectAndProviderOfViewModelBuilder());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.ironlog.app.ui.feature.calendar.CalendarViewModel
          return (T) new CalendarViewModel(singletonCImpl.bindWorkoutRepositoryProvider.get(), singletonCImpl.provideClockProvider.get());

          case 1: // com.ironlog.app.ui.feature.exercise.ExerciseHistoryViewModel
          return (T) new ExerciseHistoryViewModel(singletonCImpl.bindWorkoutRepositoryProvider.get(), singletonCImpl.bindExerciseRepositoryProvider.get(), singletonCImpl.bindSettingsRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 2: // com.ironlog.app.ui.feature.exercise.ExercisePickerViewModel
          return (T) new ExercisePickerViewModel(singletonCImpl.bindExerciseRepositoryProvider.get());

          case 3: // com.ironlog.app.ui.feature.history.HistoryViewModel
          return (T) new HistoryViewModel(singletonCImpl.bindWorkoutRepositoryProvider.get());

          case 4: // com.ironlog.app.ui.feature.home.HomeViewModel
          return (T) new HomeViewModel(singletonCImpl.bindWorkoutRepositoryProvider.get(), singletonCImpl.bindFatigueRepositoryProvider.get(), singletonCImpl.bindExerciseRepositoryProvider.get(), singletonCImpl.provideClockProvider.get());

          case 5: // com.ironlog.app.ui.MainViewModel
          return (T) new MainViewModel(singletonCImpl.bindSettingsRepositoryProvider.get());

          case 6: // com.ironlog.app.ui.feature.history.SessionDetailViewModel
          return (T) new SessionDetailViewModel(singletonCImpl.bindWorkoutRepositoryProvider.get(), singletonCImpl.bindExerciseRepositoryProvider.get(), singletonCImpl.bindSettingsRepositoryProvider.get(), singletonCImpl.provideClockProvider.get(), viewModelCImpl.savedStateHandle);

          case 7: // com.ironlog.app.ui.feature.session.SessionViewModel
          return (T) new SessionViewModel(singletonCImpl.bindWorkoutRepositoryProvider.get(), singletonCImpl.bindExerciseRepositoryProvider.get(), singletonCImpl.bindSettingsRepositoryProvider.get(), singletonCImpl.restTimerRepositoryProvider.get(), singletonCImpl.provideClockProvider.get(), viewModelCImpl.savedStateHandle);

          case 8: // com.ironlog.app.ui.feature.settings.SettingsViewModel
          return (T) new SettingsViewModel(singletonCImpl.bindSettingsRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends IronLogApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends IronLogApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends IronLogApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<IronLogDatabase> provideDatabaseProvider;

    Provider<Clock> provideClockProvider;

    Provider<IdGenerator> provideIdGeneratorProvider;

    Provider<WorkoutRepositoryImpl> workoutRepositoryImplProvider;

    Provider<WorkoutRepository> bindWorkoutRepositoryProvider;

    Provider<AlarmRestAlarmScheduler> alarmRestAlarmSchedulerProvider;

    Provider<RestAlarmScheduler> bindRestAlarmSchedulerProvider;

    Provider<RestNotifications> restNotificationsProvider;

    Provider<RestNotifier> bindRestNotifierProvider;

    Provider<ProcessForegroundState> processForegroundStateProvider;

    Provider<ElapsedClock> provideElapsedClockProvider;

    Provider<RestTimerRepository> restTimerRepositoryProvider;

    Provider<ForegroundTimerBridge> foregroundTimerBridgeProvider;

    Provider<CoroutineScope> provideApplicationScopeProvider;

    Provider<ShareImageExporter> shareImageExporterProvider;

    Provider<ExerciseRepositoryImpl> exerciseRepositoryImplProvider;

    Provider<ExerciseRepository> bindExerciseRepositoryProvider;

    Provider<DataStore<Preferences>> provideDataStoreProvider;

    Provider<SettingsDataStore> settingsDataStoreProvider;

    Provider<SettingsRepository> bindSettingsRepositoryProvider;

    Provider<FatigueRepositoryImpl> fatigueRepositoryImplProvider;

    Provider<FatigueRepository> bindFatigueRepositoryProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    MuscleGroupDao muscleGroupDao() {
      return DatabaseModule_ProvideMuscleGroupDaoFactory.provideMuscleGroupDao(provideDatabaseProvider.get());
    }

    ExerciseDao exerciseDao() {
      return DatabaseModule_ProvideExerciseDaoFactory.provideExerciseDao(provideDatabaseProvider.get());
    }

    SeedImporter seedImporter() {
      return new SeedImporter(ApplicationContextModule_ProvideContextFactory.provideContext(applicationContextModule), muscleGroupDao(), exerciseDao(), provideClockProvider.get());
    }

    WorkoutDao workoutDao() {
      return DatabaseModule_ProvideWorkoutDaoFactory.provideWorkoutDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<IronLogDatabase>(singletonCImpl, 0));
      this.provideClockProvider = DoubleCheck.provider(new SwitchingProvider<Clock>(singletonCImpl, 1));
      this.provideIdGeneratorProvider = DoubleCheck.provider(new SwitchingProvider<IdGenerator>(singletonCImpl, 5));
      this.workoutRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 4);
      this.bindWorkoutRepositoryProvider = DoubleCheck.provider((Provider) (workoutRepositoryImplProvider));
      this.alarmRestAlarmSchedulerProvider = new SwitchingProvider<>(singletonCImpl, 6);
      this.bindRestAlarmSchedulerProvider = DoubleCheck.provider((Provider) (alarmRestAlarmSchedulerProvider));
      this.restNotificationsProvider = new SwitchingProvider<>(singletonCImpl, 7);
      this.bindRestNotifierProvider = DoubleCheck.provider((Provider) (restNotificationsProvider));
      this.processForegroundStateProvider = DoubleCheck.provider(new SwitchingProvider<ProcessForegroundState>(singletonCImpl, 8));
      this.provideElapsedClockProvider = DoubleCheck.provider(new SwitchingProvider<ElapsedClock>(singletonCImpl, 9));
      this.restTimerRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RestTimerRepository>(singletonCImpl, 3));
      this.foregroundTimerBridgeProvider = DoubleCheck.provider(new SwitchingProvider<ForegroundTimerBridge>(singletonCImpl, 2));
      this.provideApplicationScopeProvider = DoubleCheck.provider(new SwitchingProvider<CoroutineScope>(singletonCImpl, 10));
      this.shareImageExporterProvider = DoubleCheck.provider(new SwitchingProvider<ShareImageExporter>(singletonCImpl, 11));
      this.exerciseRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 12);
      this.bindExerciseRepositoryProvider = DoubleCheck.provider((Provider) (exerciseRepositoryImplProvider));
      this.provideDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<DataStore<Preferences>>(singletonCImpl, 14));
      this.settingsDataStoreProvider = new SwitchingProvider<>(singletonCImpl, 13);
      this.bindSettingsRepositoryProvider = DoubleCheck.provider((Provider) (settingsDataStoreProvider));
      this.fatigueRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 15);
      this.bindFatigueRepositoryProvider = DoubleCheck.provider((Provider) (fatigueRepositoryImplProvider));
    }

    @Override
    public void injectIronLogApp(IronLogApp ironLogApp) {
      injectIronLogApp2(ironLogApp);
    }

    @Override
    public ShareImageExporter shareImageExporter() {
      return shareImageExporterProvider.get();
    }

    @Override
    public void injectRestAlarmReceiver(RestAlarmReceiver restAlarmReceiver) {
      injectRestAlarmReceiver2(restAlarmReceiver);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private IronLogApp injectIronLogApp2(IronLogApp instance) {
      IronLogApp_MembersInjector.injectSeedImporter(instance, seedImporter());
      IronLogApp_MembersInjector.injectForegroundTimerBridge(instance, foregroundTimerBridgeProvider.get());
      IronLogApp_MembersInjector.injectApplicationScope(instance, provideApplicationScopeProvider.get());
      return instance;
    }

    private RestAlarmReceiver injectRestAlarmReceiver2(RestAlarmReceiver instance2) {
      RestAlarmReceiver_MembersInjector.injectRestTimerRepository(instance2, restTimerRepositoryProvider.get());
      return instance2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.ironlog.app.data.db.IronLogDatabase
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.ironlog.app.core.time.Clock
          return (T) AppModule_Companion_ProvideClockFactory.provideClock();

          case 2: // com.ironlog.app.platform.timer.ForegroundTimerBridge
          return (T) new ForegroundTimerBridge(singletonCImpl.restTimerRepositoryProvider.get());

          case 3: // com.ironlog.app.data.timer.RestTimerRepository
          return (T) new RestTimerRepository(singletonCImpl.bindWorkoutRepositoryProvider.get(), singletonCImpl.bindRestAlarmSchedulerProvider.get(), singletonCImpl.bindRestNotifierProvider.get(), singletonCImpl.processForegroundStateProvider.get(), singletonCImpl.provideClockProvider.get(), singletonCImpl.provideElapsedClockProvider.get());

          case 4: // com.ironlog.app.data.repository.WorkoutRepositoryImpl
          return (T) new WorkoutRepositoryImpl(singletonCImpl.workoutDao(), singletonCImpl.exerciseDao(), singletonCImpl.muscleGroupDao(), singletonCImpl.provideClockProvider.get(), singletonCImpl.provideIdGeneratorProvider.get());

          case 5: // com.ironlog.app.core.id.IdGenerator
          return (T) AppModule_Companion_ProvideIdGeneratorFactory.provideIdGenerator();

          case 6: // com.ironlog.app.platform.timer.AlarmRestAlarmScheduler
          return (T) new AlarmRestAlarmScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.ironlog.app.platform.timer.RestNotifications
          return (T) new RestNotifications(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.ironlog.app.platform.timer.ProcessForegroundState
          return (T) new ProcessForegroundState();

          case 9: // com.ironlog.app.core.time.ElapsedClock
          return (T) AppModule_Companion_ProvideElapsedClockFactory.provideElapsedClock();

          case 10: // @com.ironlog.app.core.di.ApplicationScope kotlinx.coroutines.CoroutineScope
          return (T) AppModule_Companion_ProvideApplicationScopeFactory.provideApplicationScope();

          case 11: // com.ironlog.app.platform.share.ShareImageExporter
          return (T) new ShareImageExporter(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 12: // com.ironlog.app.data.repository.ExerciseRepositoryImpl
          return (T) new ExerciseRepositoryImpl(singletonCImpl.exerciseDao(), singletonCImpl.muscleGroupDao(), singletonCImpl.provideClockProvider.get(), singletonCImpl.provideIdGeneratorProvider.get());

          case 13: // com.ironlog.app.data.settings.SettingsDataStore
          return (T) new SettingsDataStore(singletonCImpl.provideDataStoreProvider.get());

          case 14: // androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>
          return (T) AppModule_Companion_ProvideDataStoreFactory.provideDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 15: // com.ironlog.app.data.repository.FatigueRepositoryImpl
          return (T) new FatigueRepositoryImpl(singletonCImpl.workoutDao(), singletonCImpl.exerciseDao(), singletonCImpl.muscleGroupDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
