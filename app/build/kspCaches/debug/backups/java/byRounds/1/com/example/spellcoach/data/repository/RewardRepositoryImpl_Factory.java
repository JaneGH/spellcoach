package com.example.spellcoach.data.repository;

import com.example.spellcoach.data.reward.RewardManager;
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
public final class RewardRepositoryImpl_Factory implements Factory<RewardRepositoryImpl> {
  private final Provider<RewardManager> rewardManagerProvider;

  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public RewardRepositoryImpl_Factory(Provider<RewardManager> rewardManagerProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.rewardManagerProvider = rewardManagerProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  @Override
  public RewardRepositoryImpl get() {
    return newInstance(rewardManagerProvider.get(), settingsDataStoreProvider.get());
  }

  public static RewardRepositoryImpl_Factory create(Provider<RewardManager> rewardManagerProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new RewardRepositoryImpl_Factory(rewardManagerProvider, settingsDataStoreProvider);
  }

  public static RewardRepositoryImpl newInstance(RewardManager rewardManager,
      SettingsDataStore settingsDataStore) {
    return new RewardRepositoryImpl(rewardManager, settingsDataStore);
  }
}
