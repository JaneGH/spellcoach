package com.example.spellcoach.data.sound;

import android.content.Context;
import com.example.spellcoach.data.settings.SettingsDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SoundEffectPlayer_Factory implements Factory<SoundEffectPlayer> {
  private final Provider<Context> contextProvider;

  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public SoundEffectPlayer_Factory(Provider<Context> contextProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.contextProvider = contextProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  @Override
  public SoundEffectPlayer get() {
    return newInstance(contextProvider.get(), settingsDataStoreProvider.get());
  }

  public static SoundEffectPlayer_Factory create(Provider<Context> contextProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new SoundEffectPlayer_Factory(contextProvider, settingsDataStoreProvider);
  }

  public static SoundEffectPlayer newInstance(Context context,
      SettingsDataStore settingsDataStore) {
    return new SoundEffectPlayer(context, settingsDataStore);
  }
}
