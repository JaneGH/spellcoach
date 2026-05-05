package com.example.spellcoach.presentation.results;

import com.example.spellcoach.data.practice.PracticeResultCache;
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
public final class ResultsViewModel_Factory implements Factory<ResultsViewModel> {
  private final Provider<PracticeResultCache> practiceResultCacheProvider;

  public ResultsViewModel_Factory(Provider<PracticeResultCache> practiceResultCacheProvider) {
    this.practiceResultCacheProvider = practiceResultCacheProvider;
  }

  @Override
  public ResultsViewModel get() {
    return newInstance(practiceResultCacheProvider.get());
  }

  public static ResultsViewModel_Factory create(
      Provider<PracticeResultCache> practiceResultCacheProvider) {
    return new ResultsViewModel_Factory(practiceResultCacheProvider);
  }

  public static ResultsViewModel newInstance(PracticeResultCache practiceResultCache) {
    return new ResultsViewModel(practiceResultCache);
  }
}
