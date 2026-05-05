package com.example.spellcoach.di;

import com.example.spellcoach.data.local.dao.SpellCoachDao;
import com.example.spellcoach.data.local.db.SpellCoachDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideDaoFactory implements Factory<SpellCoachDao> {
  private final Provider<SpellCoachDatabase> dbProvider;

  public DatabaseModule_ProvideDaoFactory(Provider<SpellCoachDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SpellCoachDao get() {
    return provideDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideDaoFactory create(Provider<SpellCoachDatabase> dbProvider) {
    return new DatabaseModule_ProvideDaoFactory(dbProvider);
  }

  public static SpellCoachDao provideDao(SpellCoachDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDao(db));
  }
}
