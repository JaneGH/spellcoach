package com.example.spellcoach.presentation.navigation;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class PracticeListHolder_Factory implements Factory<PracticeListHolder> {
  @Override
  public PracticeListHolder get() {
    return newInstance();
  }

  public static PracticeListHolder_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PracticeListHolder newInstance() {
    return new PracticeListHolder();
  }

  private static final class InstanceHolder {
    private static final PracticeListHolder_Factory INSTANCE = new PracticeListHolder_Factory();
  }
}
