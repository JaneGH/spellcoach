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
public final class CreateWordListUseCase_Factory implements Factory<CreateWordListUseCase> {
  private final Provider<WordRepository> wordRepositoryProvider;

  public CreateWordListUseCase_Factory(Provider<WordRepository> wordRepositoryProvider) {
    this.wordRepositoryProvider = wordRepositoryProvider;
  }

  @Override
  public CreateWordListUseCase get() {
    return newInstance(wordRepositoryProvider.get());
  }

  public static CreateWordListUseCase_Factory create(
      Provider<WordRepository> wordRepositoryProvider) {
    return new CreateWordListUseCase_Factory(wordRepositoryProvider);
  }

  public static CreateWordListUseCase newInstance(WordRepository wordRepository) {
    return new CreateWordListUseCase(wordRepository);
  }
}
