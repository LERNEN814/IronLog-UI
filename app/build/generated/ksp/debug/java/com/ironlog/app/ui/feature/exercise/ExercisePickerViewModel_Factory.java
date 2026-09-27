package com.ironlog.app.ui.feature.exercise;

import com.ironlog.app.domain.exercise.ExerciseRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class ExercisePickerViewModel_Factory implements Factory<ExercisePickerViewModel> {
  private final Provider<ExerciseRepository> repositoryProvider;

  private ExercisePickerViewModel_Factory(Provider<ExerciseRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ExercisePickerViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ExercisePickerViewModel_Factory create(
      Provider<ExerciseRepository> repositoryProvider) {
    return new ExercisePickerViewModel_Factory(repositoryProvider);
  }

  public static ExercisePickerViewModel newInstance(ExerciseRepository repository) {
    return new ExercisePickerViewModel(repository);
  }
}
