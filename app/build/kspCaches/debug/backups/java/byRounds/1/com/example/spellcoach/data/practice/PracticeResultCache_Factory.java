package com.example.spellcoach.data.practice;

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
public final class PracticeResultCache_Factory implements Factory<PracticeResultCache> {
  @Override
  public PracticeResultCache get() {
    return newInstance();
  }

  public static PracticeResultCache_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PracticeResultCache newInstance() {
    return new PracticeResultCache();
  }

  private static final class InstanceHolder {
    private static final PracticeResultCache_Factory INSTANCE = new PracticeResultCache_Factory();
  }
}
