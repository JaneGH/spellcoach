package com.itclimb.spellcoach.data.settings

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SettingsDataStoreLastPracticeListTest {

    private lateinit var dataStore: SettingsDataStore

    @Before
    fun setUp() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        dataStore = SettingsDataStore(context)
        dataStore.clearLastPracticeListId()
    }

    @Test
    fun setLastPracticeListId_persistsAcrossRecreation() = runTest {
        dataStore.setLastPracticeListId(42L)

        val recreated = SettingsDataStore(InstrumentationRegistry.getInstrumentation().targetContext)

        assertThat(recreated.lastPracticeListId.first()).isEqualTo(42L)
    }

    @Test
    fun clearLastPracticeListId_removesPersistedValue() = runTest {
        dataStore.setLastPracticeListId(42L)
        dataStore.clearLastPracticeListId()

        assertThat(dataStore.lastPracticeListId.first()).isNull()
    }
}
