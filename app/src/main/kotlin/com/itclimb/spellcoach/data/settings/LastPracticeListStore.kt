package com.itclimb.spellcoach.data.settings

import kotlinx.coroutines.flow.Flow

interface LastPracticeListStore {
    val lastPracticeListId: Flow<Long?>

    suspend fun setLastPracticeListId(id: Long)

    suspend fun clearLastPracticeListId()
}
