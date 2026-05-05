package com.example.spellcoach;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.example.spellcoach.data.local.dao.SpellCoachDao;
import com.example.spellcoach.data.local.db.SpellCoachDatabase;
import com.example.spellcoach.data.practice.PracticeResultCache;
import com.example.spellcoach.data.repository.RewardRepositoryImpl;
import com.example.spellcoach.data.repository.SettingsRepositoryImpl;
import com.example.spellcoach.data.repository.WordRepositoryImpl;
import com.example.spellcoach.data.reward.RewardManager;
import com.example.spellcoach.data.settings.SettingsDataStore;
import com.example.spellcoach.data.sound.SoundEffectPlayer;
import com.example.spellcoach.data.tts.TtsManager;
import com.example.spellcoach.di.CoroutineScopeModule_ProvideApplicationScopeFactory;
import com.example.spellcoach.di.CoroutineScopeModule_ProvideIoDispatcherFactory;
import com.example.spellcoach.di.DatabaseModule_ProvideDaoFactory;
import com.example.spellcoach.di.DatabaseModule_ProvideDatabaseFactory;
import com.example.spellcoach.domain.usecase.CreateWordListUseCase;
import com.example.spellcoach.domain.usecase.ObserveSettingsUseCase;
import com.example.spellcoach.domain.usecase.ObserveWordListsUseCase;
import com.example.spellcoach.domain.usecase.ProcessSpellingResultUseCase;
import com.example.spellcoach.domain.usecase.UpdateSettingsUseCase;
import com.example.spellcoach.presentation.addwords.AddWordsViewModel;
import com.example.spellcoach.presentation.addwords.AddWordsViewModel_HiltModules;
import com.example.spellcoach.presentation.navigation.PracticeListHolder;
import com.example.spellcoach.presentation.practice.PracticeViewModel;
import com.example.spellcoach.presentation.practice.PracticeViewModel_HiltModules;
import com.example.spellcoach.presentation.results.ResultsViewModel;
import com.example.spellcoach.presentation.results.ResultsViewModel_HiltModules;
import com.example.spellcoach.presentation.settings.SettingsViewModel;
import com.example.spellcoach.presentation.settings.SettingsViewModel_HiltModules;
import com.example.spellcoach.presentation.wordlists.WordListsViewModel;
import com.example.spellcoach.presentation.wordlists.WordListsViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineScope;

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
public final class DaggerSpellCoachApplication_HiltComponents_SingletonC {
  private DaggerSpellCoachApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public SpellCoachApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements SpellCoachApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public SpellCoachApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements SpellCoachApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public SpellCoachApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements SpellCoachApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public SpellCoachApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements SpellCoachApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SpellCoachApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements SpellCoachApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SpellCoachApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements SpellCoachApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public SpellCoachApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements SpellCoachApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public SpellCoachApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends SpellCoachApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends SpellCoachApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends SpellCoachApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends SpellCoachApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(5).put(LazyClassKeyProvider.com_example_spellcoach_presentation_addwords_AddWordsViewModel, AddWordsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_example_spellcoach_presentation_practice_PracticeViewModel, PracticeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_example_spellcoach_presentation_results_ResultsViewModel, ResultsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_example_spellcoach_presentation_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_example_spellcoach_presentation_wordlists_WordListsViewModel, WordListsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_example_spellcoach_presentation_results_ResultsViewModel = "com.example.spellcoach.presentation.results.ResultsViewModel";

      static String com_example_spellcoach_presentation_settings_SettingsViewModel = "com.example.spellcoach.presentation.settings.SettingsViewModel";

      static String com_example_spellcoach_presentation_practice_PracticeViewModel = "com.example.spellcoach.presentation.practice.PracticeViewModel";

      static String com_example_spellcoach_presentation_wordlists_WordListsViewModel = "com.example.spellcoach.presentation.wordlists.WordListsViewModel";

      static String com_example_spellcoach_presentation_addwords_AddWordsViewModel = "com.example.spellcoach.presentation.addwords.AddWordsViewModel";

      @KeepFieldType
      ResultsViewModel com_example_spellcoach_presentation_results_ResultsViewModel2;

      @KeepFieldType
      SettingsViewModel com_example_spellcoach_presentation_settings_SettingsViewModel2;

      @KeepFieldType
      PracticeViewModel com_example_spellcoach_presentation_practice_PracticeViewModel2;

      @KeepFieldType
      WordListsViewModel com_example_spellcoach_presentation_wordlists_WordListsViewModel2;

      @KeepFieldType
      AddWordsViewModel com_example_spellcoach_presentation_addwords_AddWordsViewModel2;
    }
  }

  private static final class ViewModelCImpl extends SpellCoachApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AddWordsViewModel> addWordsViewModelProvider;

    private Provider<PracticeViewModel> practiceViewModelProvider;

    private Provider<ResultsViewModel> resultsViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<WordListsViewModel> wordListsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private CreateWordListUseCase createWordListUseCase() {
      return new CreateWordListUseCase(singletonCImpl.wordRepositoryImplProvider.get());
    }

    private ProcessSpellingResultUseCase processSpellingResultUseCase() {
      return new ProcessSpellingResultUseCase(singletonCImpl.wordRepositoryImplProvider.get());
    }

    private ObserveSettingsUseCase observeSettingsUseCase() {
      return new ObserveSettingsUseCase(singletonCImpl.settingsRepositoryImplProvider.get());
    }

    private UpdateSettingsUseCase updateSettingsUseCase() {
      return new UpdateSettingsUseCase(singletonCImpl.settingsRepositoryImplProvider.get());
    }

    private ObserveWordListsUseCase observeWordListsUseCase() {
      return new ObserveWordListsUseCase(singletonCImpl.wordRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.addWordsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.practiceViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.resultsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.wordListsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(5).put(LazyClassKeyProvider.com_example_spellcoach_presentation_addwords_AddWordsViewModel, ((Provider) addWordsViewModelProvider)).put(LazyClassKeyProvider.com_example_spellcoach_presentation_practice_PracticeViewModel, ((Provider) practiceViewModelProvider)).put(LazyClassKeyProvider.com_example_spellcoach_presentation_results_ResultsViewModel, ((Provider) resultsViewModelProvider)).put(LazyClassKeyProvider.com_example_spellcoach_presentation_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_example_spellcoach_presentation_wordlists_WordListsViewModel, ((Provider) wordListsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_example_spellcoach_presentation_addwords_AddWordsViewModel = "com.example.spellcoach.presentation.addwords.AddWordsViewModel";

      static String com_example_spellcoach_presentation_results_ResultsViewModel = "com.example.spellcoach.presentation.results.ResultsViewModel";

      static String com_example_spellcoach_presentation_wordlists_WordListsViewModel = "com.example.spellcoach.presentation.wordlists.WordListsViewModel";

      static String com_example_spellcoach_presentation_settings_SettingsViewModel = "com.example.spellcoach.presentation.settings.SettingsViewModel";

      static String com_example_spellcoach_presentation_practice_PracticeViewModel = "com.example.spellcoach.presentation.practice.PracticeViewModel";

      @KeepFieldType
      AddWordsViewModel com_example_spellcoach_presentation_addwords_AddWordsViewModel2;

      @KeepFieldType
      ResultsViewModel com_example_spellcoach_presentation_results_ResultsViewModel2;

      @KeepFieldType
      WordListsViewModel com_example_spellcoach_presentation_wordlists_WordListsViewModel2;

      @KeepFieldType
      SettingsViewModel com_example_spellcoach_presentation_settings_SettingsViewModel2;

      @KeepFieldType
      PracticeViewModel com_example_spellcoach_presentation_practice_PracticeViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.example.spellcoach.presentation.addwords.AddWordsViewModel 
          return (T) new AddWordsViewModel(viewModelCImpl.createWordListUseCase());

          case 1: // com.example.spellcoach.presentation.practice.PracticeViewModel 
          return (T) new PracticeViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.wordRepositoryImplProvider.get(), viewModelCImpl.processSpellingResultUseCase(), viewModelCImpl.observeSettingsUseCase(), singletonCImpl.rewardRepositoryImplProvider.get(), singletonCImpl.practiceResultCacheProvider.get(), singletonCImpl.soundEffectPlayerProvider.get(), singletonCImpl.ttsManagerProvider.get());

          case 2: // com.example.spellcoach.presentation.results.ResultsViewModel 
          return (T) new ResultsViewModel(singletonCImpl.practiceResultCacheProvider.get());

          case 3: // com.example.spellcoach.presentation.settings.SettingsViewModel 
          return (T) new SettingsViewModel(viewModelCImpl.observeSettingsUseCase(), viewModelCImpl.updateSettingsUseCase(), singletonCImpl.ttsManagerProvider.get());

          case 4: // com.example.spellcoach.presentation.wordlists.WordListsViewModel 
          return (T) new WordListsViewModel(viewModelCImpl.observeWordListsUseCase(), singletonCImpl.wordRepositoryImplProvider.get(), singletonCImpl.practiceListHolderProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends SpellCoachApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends SpellCoachApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends SpellCoachApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<PracticeListHolder> practiceListHolderProvider;

    private Provider<SpellCoachDatabase> provideDatabaseProvider;

    private Provider<WordRepositoryImpl> wordRepositoryImplProvider;

    private Provider<SettingsDataStore> settingsDataStoreProvider;

    private Provider<SettingsRepositoryImpl> settingsRepositoryImplProvider;

    private Provider<RewardManager> rewardManagerProvider;

    private Provider<RewardRepositoryImpl> rewardRepositoryImplProvider;

    private Provider<PracticeResultCache> practiceResultCacheProvider;

    private Provider<SoundEffectPlayer> soundEffectPlayerProvider;

    private Provider<CoroutineScope> provideApplicationScopeProvider;

    private Provider<TtsManager> ttsManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private SpellCoachDao spellCoachDao() {
      return DatabaseModule_ProvideDaoFactory.provideDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.practiceListHolderProvider = DoubleCheck.provider(new SwitchingProvider<PracticeListHolder>(singletonCImpl, 0));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<SpellCoachDatabase>(singletonCImpl, 2));
      this.wordRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<WordRepositoryImpl>(singletonCImpl, 1));
      this.settingsDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<SettingsDataStore>(singletonCImpl, 4));
      this.settingsRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<SettingsRepositoryImpl>(singletonCImpl, 3));
      this.rewardManagerProvider = DoubleCheck.provider(new SwitchingProvider<RewardManager>(singletonCImpl, 6));
      this.rewardRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<RewardRepositoryImpl>(singletonCImpl, 5));
      this.practiceResultCacheProvider = DoubleCheck.provider(new SwitchingProvider<PracticeResultCache>(singletonCImpl, 7));
      this.soundEffectPlayerProvider = DoubleCheck.provider(new SwitchingProvider<SoundEffectPlayer>(singletonCImpl, 8));
      this.provideApplicationScopeProvider = DoubleCheck.provider(new SwitchingProvider<CoroutineScope>(singletonCImpl, 10));
      this.ttsManagerProvider = DoubleCheck.provider(new SwitchingProvider<TtsManager>(singletonCImpl, 9));
    }

    @Override
    public void injectSpellCoachApplication(SpellCoachApplication spellCoachApplication) {
    }

    @Override
    public PracticeListHolder practiceListHolder() {
      return practiceListHolderProvider.get();
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.example.spellcoach.presentation.navigation.PracticeListHolder 
          return (T) new PracticeListHolder();

          case 1: // com.example.spellcoach.data.repository.WordRepositoryImpl 
          return (T) new WordRepositoryImpl(singletonCImpl.spellCoachDao());

          case 2: // com.example.spellcoach.data.local.db.SpellCoachDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.example.spellcoach.data.repository.SettingsRepositoryImpl 
          return (T) new SettingsRepositoryImpl(singletonCImpl.settingsDataStoreProvider.get());

          case 4: // com.example.spellcoach.data.settings.SettingsDataStore 
          return (T) new SettingsDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.example.spellcoach.data.repository.RewardRepositoryImpl 
          return (T) new RewardRepositoryImpl(singletonCImpl.rewardManagerProvider.get(), singletonCImpl.settingsDataStoreProvider.get());

          case 6: // com.example.spellcoach.data.reward.RewardManager 
          return (T) new RewardManager(singletonCImpl.settingsDataStoreProvider.get());

          case 7: // com.example.spellcoach.data.practice.PracticeResultCache 
          return (T) new PracticeResultCache();

          case 8: // com.example.spellcoach.data.sound.SoundEffectPlayer 
          return (T) new SoundEffectPlayer(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.settingsDataStoreProvider.get());

          case 9: // com.example.spellcoach.data.tts.TtsManager 
          return (T) new TtsManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideApplicationScopeProvider.get());

          case 10: // @com.example.spellcoach.di.ApplicationScope kotlinx.coroutines.CoroutineScope 
          return (T) CoroutineScopeModule_ProvideApplicationScopeFactory.provideApplicationScope(CoroutineScopeModule_ProvideIoDispatcherFactory.provideIoDispatcher());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
