package com.itclimb.spellcoach.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.itclimb.spellcoach.domain.model.AppSettings
import com.itclimb.spellcoach.domain.model.MistakeBehavior
import com.itclimb.spellcoach.domain.model.ThemePreference
import com.itclimb.spellcoach.domain.model.RewardState
import com.itclimb.spellcoach.domain.model.Badge
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "spellcoach_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val requiredCorrect = intPreferencesKey("required_correct_answers")
        val mistakeBehavior = stringPreferencesKey("mistake_behavior")
        val audioEnabled = booleanPreferencesKey("audio_enabled")
        val letterHintsEnabled = booleanPreferencesKey("letter_hints_enabled")
        val speechRate = floatPreferencesKey("speech_rate")
        val rewardSounds = booleanPreferencesKey("reward_sounds")
        val animations = booleanPreferencesKey("animations")
        val themePreference = stringPreferencesKey("theme_preference")
        val totalCorrectLifetime = intPreferencesKey("total_correct_lifetime")
        val currentStreak = intPreferencesKey("current_streak")
        val longestStreak = intPreferencesKey("longest_streak")
        val lastPracticeDay = longPreferencesKey("last_practice_day_epoch")
        val unlockedBadges = stringPreferencesKey("unlocked_badges")
    }

    val appSettings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            requiredCorrectAnswers = prefs[Keys.requiredCorrect] ?: 3,
            mistakeBehavior = prefs[Keys.mistakeBehavior]?.let {
                runCatching { MistakeBehavior.valueOf(it) }.getOrDefault(MistakeBehavior.DECREASE_PROGRESS)
            } ?: MistakeBehavior.DECREASE_PROGRESS,
            audioEnabled = prefs[Keys.audioEnabled] ?: true,
            letterHintsEnabled = prefs[Keys.letterHintsEnabled] ?: true,
            speechRate = prefs[Keys.speechRate] ?: 1f,
            rewardSoundsEnabled = prefs[Keys.rewardSounds] ?: true,
            animationsEnabled = prefs[Keys.animations] ?: true,
            themePreference = prefs[Keys.themePreference]?.let {
                runCatching { ThemePreference.valueOf(it) }.getOrDefault(ThemePreference.SYSTEM)
            } ?: ThemePreference.SYSTEM
        )
    }

    val rewardState: Flow<RewardState> = context.dataStore.data.map { prefs ->
        val badgeNames = prefs[Keys.unlockedBadges]?.split(",").orEmpty().filter { it.isNotBlank() }
        val badges = badgeNames.mapNotNull { runCatching { Badge.valueOf(it) }.getOrNull() }.toSet()
        RewardState(
            totalCorrectLifetime = prefs[Keys.totalCorrectLifetime] ?: 0,
            currentStreak = prefs[Keys.currentStreak] ?: 0,
            longestStreak = prefs[Keys.longestStreak] ?: 0,
            lastPracticeDayEpochDay = prefs[Keys.lastPracticeDay] ?: 0L,
            unlockedBadges = badges
        )
    }

    suspend fun updateAppSettings(transform: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val cur = AppSettings(
                requiredCorrectAnswers = prefs[Keys.requiredCorrect] ?: 3,
                mistakeBehavior = prefs[Keys.mistakeBehavior]?.let {
                    runCatching { MistakeBehavior.valueOf(it) }.getOrDefault(MistakeBehavior.DECREASE_PROGRESS)
                } ?: MistakeBehavior.DECREASE_PROGRESS,
                audioEnabled = prefs[Keys.audioEnabled] ?: true,
                letterHintsEnabled = prefs[Keys.letterHintsEnabled] ?: true,
                speechRate = prefs[Keys.speechRate] ?: 1f,
                rewardSoundsEnabled = prefs[Keys.rewardSounds] ?: true,
                animationsEnabled = prefs[Keys.animations] ?: true,
                themePreference = prefs[Keys.themePreference]?.let {
                    runCatching { ThemePreference.valueOf(it) }.getOrDefault(ThemePreference.SYSTEM)
                } ?: ThemePreference.SYSTEM
            )
            val next = transform(cur)
            prefs[Keys.requiredCorrect] = next.requiredCorrectAnswers
            prefs[Keys.mistakeBehavior] = next.mistakeBehavior.name
            prefs[Keys.audioEnabled] = next.audioEnabled
            prefs[Keys.letterHintsEnabled] = next.letterHintsEnabled
            prefs[Keys.speechRate] = next.speechRate
            prefs[Keys.rewardSounds] = next.rewardSoundsEnabled
            prefs[Keys.animations] = next.animationsEnabled
            prefs[Keys.themePreference] = next.themePreference.name
        }
    }

    suspend fun updateRewardState(transform: (RewardState) -> RewardState) {
        context.dataStore.edit { prefs ->
            val cur = RewardState(
                totalCorrectLifetime = prefs[Keys.totalCorrectLifetime] ?: 0,
                currentStreak = prefs[Keys.currentStreak] ?: 0,
                longestStreak = prefs[Keys.longestStreak] ?: 0,
                lastPracticeDayEpochDay = prefs[Keys.lastPracticeDay] ?: 0L,
                unlockedBadges = prefs[Keys.unlockedBadges]?.split(",").orEmpty()
                    .filter { it.isNotBlank() }
                    .mapNotNull { runCatching { Badge.valueOf(it) }.getOrNull() }
                    .toSet()
            )
            val next = transform(cur)
            prefs[Keys.totalCorrectLifetime] = next.totalCorrectLifetime
            prefs[Keys.currentStreak] = next.currentStreak
            prefs[Keys.longestStreak] = next.longestStreak
            prefs[Keys.lastPracticeDay] = next.lastPracticeDayEpochDay
            prefs[Keys.unlockedBadges] = next.unlockedBadges.joinToString(",") { it.name }
        }
    }
}
