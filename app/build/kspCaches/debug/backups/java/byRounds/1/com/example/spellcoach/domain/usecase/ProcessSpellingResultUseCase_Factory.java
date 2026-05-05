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
public final class ProcessSpellingResultUseCase_Factory implements Factory<ProcessSpellingResultUseCase> {
  private final Provider<WordRepository> wordRepositoryProvider;

  public ProcessSpellingResultUseCase_Factory(Provider<WordRepository> wordRepositoryProvider) {
    this.wordRepositoryProvider = wordRepositoryProvider;
  }

  @Override
  public ProcessSpellingResultUseCase get() {
    return newInstance(wordRepositoryProvider.get());
  }

  public static ProcessSpellingResultUseCase_Factory create(
      Provider<WordRepository> wordRepositoryProvider) {
    return new ProcessSpellingResultUseCase_Factory(wordRepositoryProvider);
  }

  public static ProcessSpellingResultUseCase newInstance(WordRepository wordRepository) {
    return new ProcessSpellingResultUseCase(wordRepository);
  }
}
