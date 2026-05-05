package com.example.spellcoach.presentation.practice;

import androidx.lifecycle.SavedStateHandle;
import com.example.spellcoach.data.practice.PracticeResultCache;
import com.example.spellcoach.data.sound.SoundEffectPlayer;
import com.example.spellcoach.data.tts.TtsManager;
import com.example.spellcoach.domain.repository.RewardRepository;
import com.example.spellcoach.domain.repository.WordRepository;
import com.example.spellcoach.domain.usecase.ObserveSettingsUseCase;
import com.example.spellcoach.domain.usecase.ProcessSpellingResultUseCase;
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
public final class PracticeViewModel_Factory implements Factory<PracticeViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<WordRepository> wordRepositoryProvider;

  private final Provider<ProcessSpellingResultUseCase> processSpellingProvider;

  private final Provider<ObserveSettingsUseCase> observeSettingsUseCaseProvider;

  private final Provider<RewardRepository> rewardRepositoryProvider;

  private final Provider<PracticeResultCache> practiceResultCacheProvider;

  private final Provider<SoundEffectPlayer> soundProvider;

  private final Provider<TtsManager> ttsProvider;

  public PracticeViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<WordRepository> wordRepositoryProvider,
      Provider<ProcessSpellingResultUseCase> processSpellingProvider,
      Provider<ObserveSettingsUseCase> observeSettingsUseCaseProvider,
      Provider<RewardRepository> rewardRepositoryProvider,
      Provider<PracticeResultCache> practiceResultCacheProvider,
      Provider<SoundEffectPlayer> soundProvider, Provider<TtsManager> ttsProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.wordRepositoryProvider = wordRepositoryProvider;
    this.processSpellingProvider = processSpellingProvider;
    this.observeSettingsUseCaseProvider = observeSettingsUseCaseProvider;
    this.rewardRepositoryProvider = rewardRepositoryProvider;
    this.practiceResultCacheProvider = practiceResultCacheProvider;
    this.soundProvider = soundProvider;
    this.ttsProvider = ttsProvider;
  }

  @Override
  public PracticeViewModel get() {
    return newInstance(savedStateHandleProvider.get(), wordRepositoryProvider.get(), processSpellingProvider.get(), observeSettingsUseCaseProvider.get(), rewardRepositoryProvider.get(), practiceResultCacheProvider.get(), soundProvider.get(), ttsProvider.get());
  }

  public static PracticeViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<WordRepository> wordRepositoryProvider,
      Provider<ProcessSpellingResultUseCase> processSpellingProvider,
      Provider<ObserveSettingsUseCase> observeSettingsUseCaseProvider,
      Provider<RewardRepository> rewardRepositoryProvider,
      Provider<PracticeResultCache> practiceResultCacheProvider,
      Provider<SoundEffectPlayer> soundProvider, Provider<TtsManager> ttsProvider) {
    return new PracticeViewModel_Factory(savedStateHandleProvider, wordRepositoryProvider, processSpellingProvider, observeSettingsUseCaseProvider, rewardRepositoryProvider, practiceResultCacheProvider, soundProvider, ttsProvider);
  }

  public static PracticeViewModel newInstance(SavedStateHandle savedStateHandle,
      WordRepository wordRepository, ProcessSpellingResultUseCase processSpelling,
      ObserveSettingsUseCase observeSettingsUseCase, RewardRepository rewardRepository,
      PracticeResultCache practiceResultCache, SoundEffectPlayer sound, TtsManager tts) {
    return new PracticeViewModel(savedStateHandle, wordRepository, processSpelling, observeSettingsUseCase, rewardRepository, practiceResultCache, sound, tts);
  }
}
