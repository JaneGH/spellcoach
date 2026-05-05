package com.example.spellcoach.data.reward;

import com.example.spellcoach.data.settings.SettingsDataStore;
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
public final class RewardManager_Factory implements Factory<RewardManager> {
  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public RewardManager_Factory(Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  @Override
  public RewardManager get() {
    return newInstance(settingsDataStoreProvider.get());
  }

  public static RewardManager_Factory create(
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new RewardManager_Factory(settingsDataStoreProvider);
  }

  public static RewardManager newInstance(SettingsDataStore settingsDataStore) {
    return new RewardManager(settingsDataStore);
  }
}
