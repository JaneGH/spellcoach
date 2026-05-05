package com.example.spellcoach.data.repository;

import com.example.spellcoach.data.local.dao.SpellCoachDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class WordRepositoryImpl_Factory implements Factory<WordRepositoryImpl> {
  private final Provider<SpellCoachDao> daoProvider;

  public WordRepositoryImpl_Factory(Provider<SpellCoachDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public WordRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static WordRepositoryImpl_Factory create(Provider<SpellCoachDao> daoProvider) {
    return new WordRepositoryImpl_Factory(daoProvider);
  }

  public static WordRepositoryImpl newInstance(SpellCoachDao dao) {
    return new WordRepositoryImpl(dao);
  }
}
