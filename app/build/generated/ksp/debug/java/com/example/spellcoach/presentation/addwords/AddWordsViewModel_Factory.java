package com.example.spellcoach.presentation.addwords;

import com.example.spellcoach.domain.usecase.CreateWordListUseCase;
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
public final class AddWordsViewModel_Factory implements Factory<AddWordsViewModel> {
  private final Provider<CreateWordListUseCase> createWordListProvider;

  public AddWordsViewModel_Factory(Provider<CreateWordListUseCase> createWordListProvider) {
    this.createWordListProvider = createWordListProvider;
  }

  @Override
  public AddWordsViewModel get() {
    return newInstance(createWordListProvider.get());
  }

  public static AddWordsViewModel_Factory create(
      Provider<CreateWordListUseCase> createWordListProvider) {
    return new AddWordsViewModel_Factory(createWordListProvider);
  }

  public static AddWordsViewModel newInstance(CreateWordListUseCase createWordList) {
    return new AddWordsViewModel(createWordList);
  }
}
