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
public final class ObserveWordListsUseCase_Factory implements Factory<ObserveWordListsUseCase> {
  private final Provider<WordRepository> wordRepositoryProvider;

  public ObserveWordListsUseCase_Factory(Provider<WordRepository> wordRepositoryProvider) {
    this.wordRepositoryProvider = wordRepositoryProvider;
  }

  @Override
  public ObserveWordListsUseCase get() {
    return newInstance(wordRepositoryProvider.get());
  }

  public static ObserveWordListsUseCase_Factory create(
      Provider<WordRepository> wordRepositoryProvider) {
    return new ObserveWordListsUseCase_Factory(wordRepositoryProvider);
  }

  public static ObserveWordListsUseCase newInstance(WordRepository wordRepository) {
    return new ObserveWordListsUseCase(wordRepository);
  }
}
