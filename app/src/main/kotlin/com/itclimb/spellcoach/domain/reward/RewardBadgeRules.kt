package com.itclimb.spellcoach.domain.reward

import com.itclimb.spellcoach.domain.model.Badge
import com.itclimb.spellcoach.domain.model.RewardState
import java.time.Instant
import java.time.ZoneId

data class RewardTransition(
    val nextState: RewardState,
    val newBadges: List<Badge>
)


object RewardBadgeRules {

    fun onCorrectAnswer(state: RewardState, isFirstLifetimeCorrect: Boolean): RewardTransition {
        val newBadges = mutableListOf<Badge>()
        val total = state.totalCorrectLifetime + 1
        val unlocked = state.unlockedBadges.toMutableSet()
        if (isFirstLifetimeCorrect && unlocked.add(Badge.FIRST_WORD)) {
            newBadges.add(Badge.FIRST_WORD)
        }
        if (total >= 10 && unlocked.add(Badge.TEN_CORRECT)) {
            newBadges.add(Badge.TEN_CORRECT)
        }
        if (total >= 100 && unlocked.add(Badge.WORD_WIZARD)) {
            newBadges.add(Badge.WORD_WIZARD)
        }
        return RewardTransition(
            nextState = state.copy(
                totalCorrectLifetime = total,
                unlockedBadges = unlocked
            ),
            newBadges = newBadges
        )
    }

    fun onSessionCompleted(
        state: RewardState,
        allCorrect: Boolean,
        totalWords: Int
    ): RewardTransition {
        val newBadges = mutableListOf<Badge>()
        val unlocked = state.unlockedBadges.toMutableSet()
        if (allCorrect && totalWords > 0 && unlocked.add(Badge.PERFECT_PRACTICE)) {
            newBadges.add(Badge.PERFECT_PRACTICE)
        }
        val today = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
        val last = state.lastPracticeDayEpochDay
        var streak = state.currentStreak
        when {
            last == 0L -> streak = 1
            today == last -> { /* same calendar day */ }
            today == last + 1 -> streak += 1
            else -> streak = 1
        }
        if (streak >= 3 && unlocked.add(Badge.THREE_DAY_STREAK)) {
            newBadges.add(Badge.THREE_DAY_STREAK)
        }
        return RewardTransition(
            nextState = state.copy(
                currentStreak = streak,
                longestStreak = maxOf(state.longestStreak, streak),
                lastPracticeDayEpochDay = today,
                unlockedBadges = unlocked
            ),
            newBadges = newBadges
        )
    }
}
