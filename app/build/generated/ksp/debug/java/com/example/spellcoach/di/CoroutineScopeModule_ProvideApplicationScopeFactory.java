package com.example.spellcoach.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "com.example.spellcoach.di.ApplicationScope",
    "com.example.spellcoach.di.IoDispatcher"
})
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
public final class CoroutineScopeModule_ProvideApplicationScopeFactory implements Factory<CoroutineScope> {
  private final Provider<CoroutineDispatcher> ioProvider;

  public CoroutineScopeModule_ProvideApplicationScopeFactory(
      Provider<CoroutineDispatcher> ioProvider) {
    this.ioProvider = ioProvider;
  }

  @Override
  public CoroutineScope get() {
    return provideApplicationScope(ioProvider.get());
  }

  public static CoroutineScopeModule_ProvideApplicationScopeFactory create(
      Provider<CoroutineDispatcher> ioProvider) {
    return new CoroutineScopeModule_ProvideApplicationScopeFactory(ioProvider);
  }

  public static CoroutineScope provideApplicationScope(CoroutineDispatcher io) {
    return Preconditions.checkNotNullFromProvides(CoroutineScopeModule.INSTANCE.provideApplicationScope(io));
  }
}
