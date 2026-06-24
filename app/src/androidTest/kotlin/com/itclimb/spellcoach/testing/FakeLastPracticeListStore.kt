package com.itclimb.spellcoach.testing

import com.itclimb.spellcoach.data.settings.LastPracticeListStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeLastPracticeListStore(
    initial: Long? = null
) : LastPracticeListStore {
    private val stored = MutableStateFlow(initial)

    override val lastPracticeListId: Flow<Long?> = stored.asStateFlow()

    override suspend fun setLastPracticeListId(id: Long) {
        stored.value = id
    }

    override suspend fun clearLastPracticeListId() {
        stored.value = null
    }
}
