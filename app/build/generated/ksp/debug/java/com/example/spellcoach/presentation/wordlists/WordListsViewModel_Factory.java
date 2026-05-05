package com.example.spellcoach.presentation.wordlists;

import com.example.spellcoach.domain.repository.WordRepository;
import com.example.spellcoach.domain.usecase.ObserveWordListsUseCase;
import com.example.spellcoach.presentation.navigation.PracticeListHolder;
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
public final class WordListsViewModel_Factory implements Factory<WordListsViewModel> {
  private final Provider<ObserveWordListsUseCase> observeWordListsProvider;

  private final Provider<WordRepository> wordRepositoryProvider;

  private final Provider<PracticeListHolder> practiceListHolderProvider;

  public WordListsViewModel_Factory(Provider<ObserveWordListsUseCase> observeWordListsProvider,
      Provider<WordRepository> wordRepositoryProvider,
      Provider<PracticeListHolder> practiceListHolderProvider) {
    this.observeWordListsProvider = observeWordListsProvider;
    this.wordRepositoryProvider = wordRepositoryProvider;
    this.practiceListHolderProvider = practiceListHolderProvider;
  }

  @Override
  public WordListsViewModel get() {
    return newInstance(observeWordListsProvider.get(), wordRepositoryProvider.get(), practiceListHolderProvider.get());
  }

  public static WordListsViewModel_Factory create(
      Provider<ObserveWordListsUseCase> observeWordListsProvider,
      Provider<WordRepository> wordRepositoryProvider,
      Provider<PracticeListHolder> practiceListHolderProvider) {
    return new WordListsViewModel_Factory(observeWordListsProvider, wordRepositoryProvider, practiceListHolderProvider);
  }

  public static WordListsViewModel newInstance(ObserveWordListsUseCase observeWordLists,
      WordRepository wordRepository, PracticeListHolder practiceListHolder) {
    return new WordListsViewModel(observeWordLists, wordRepository, practiceListHolder);
  }
}
