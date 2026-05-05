package com.example.spellcoach.data.tts;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "dagger.hilt.android.qualifiers.ApplicationContext",
    "com.example.spellcoach.di.ApplicationScope"
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
public final class TtsManager_Factory implements Factory<TtsManager> {
  private final Provider<Context> contextProvider;

  private final Provider<CoroutineScope> applicationScopeProvider;

  public TtsManager_Factory(Provider<Context> contextProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    this.contextProvider = contextProvider;
    this.applicationScopeProvider = applicationScopeProvider;
  }

  @Override
  public TtsManager get() {
    return newInstance(contextProvider.get(), applicationScopeProvider.get());
  }

  public static TtsManager_Factory create(Provider<Context> contextProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    return new TtsManager_Factory(contextProvider, applicationScopeProvider);
  }

  public static TtsManager newInstance(Context context, CoroutineScope applicationScope) {
    return new TtsManager(context, applicationScope);
  }
}
