package com.example.spellcoach.domain.usecase;

import com.example.spellcoach.domain.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "deprecation"
})
public final class ObserveSettingsUseCase_Factory implements Factory<ObserveSettingsUseCase> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public ObserveSettingsUseCase_Factory(Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public ObserveSettingsUseCase get() {
    return newInstance(settingsRepositoryProvider.get());
  }

  public static ObserveSettingsUseCase_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new ObserveSettingsUseCase_Factory(settingsRepositoryProvider);
  }

  public static ObserveSettingsUseCase newInstance(SettingsRepository settingsRepository) {
    return new ObserveSettingsUseCase(settingsRepository);
  }
}
