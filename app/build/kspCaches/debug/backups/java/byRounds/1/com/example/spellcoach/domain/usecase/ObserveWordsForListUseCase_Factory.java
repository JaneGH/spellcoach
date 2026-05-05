package com.example.spellcoach.domain.usecase;

import com.example.spellcoach.domain.repository.WordRepository;
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
public final class ObserveWordsForListUseCase_Factory implements Factory<ObserveWordsForListUseCase> {
  private final Provider<WordRepository> wordRepositoryProvider;

  public ObserveWordsForListUseCase_Factory(Provider<WordRepository> wordRepositoryProvider) {
    this.wordRepositoryProvider = wordRepositoryProvider;
  }

  @Override
  public ObserveWordsForListUseCase get() {
    return newInstance(wordRepositoryProvider.get());
  }

  public static ObserveWordsForListUseCase_Factory create(
      Provider<WordRepository> wordRepositoryProvider) {
    return new ObserveWordsForListUseCase_Factory(wordRepositoryProvider);
  }

  public static ObserveWordsForListUseCase newInstance(WordRepository wordRepository) {
    return new ObserveWordsForListUseCase(wordRepository);
  }
}
