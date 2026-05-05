package com.example.spellcoach.presentation.settings;

import com.example.spellcoach.data.tts.TtsManager;
import com.example.spellcoach.domain.usecase.ObserveSettingsUseCase;
import com.example.spellcoach.domain.usecase.UpdateSettingsUseCase;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<ObserveSettingsUseCase> observeSettingsUseCaseProvider;

  private final Provider<UpdateSettingsUseCase> updateSettingsProvider;

  private final Provider<TtsManager> ttsManagerProvider;

  public SettingsViewModel_Factory(Provider<ObserveSettingsUseCase> observeSettingsUseCaseProvider,
      Provider<UpdateSettingsUseCase> updateSettingsProvider,
      Provider<TtsManager> ttsManagerProvider) {
    this.observeSettingsUseCaseProvider = observeSettingsUseCaseProvider;
    this.updateSettingsProvider = updateSettingsProvider;
    this.ttsManagerProvider = ttsManagerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(observeSettingsUseCaseProvider.get(), updateSettingsProvider.get(), ttsManagerProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<ObserveSettingsUseCase> observeSettingsUseCaseProvider,
      Provider<UpdateSettingsUseCase> updateSettingsProvider,
      Provider<TtsManager> ttsManagerProvider) {
    return new SettingsViewModel_Factory(observeSettingsUseCaseProvider, updateSettingsProvider, ttsManagerProvider);
  }

  public static SettingsViewModel newInstance(ObserveSettingsUseCase observeSettingsUseCase,
      UpdateSettingsUseCase updateSettings, TtsManager ttsManager) {
    return new SettingsViewModel(observeSettingsUseCase, updateSettings, ttsManager);
  }
}
