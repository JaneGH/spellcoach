package com.itclimb.spellcoach.data.local.db

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpellCoachMigration3To4Test {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SpellCoachDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migrate3To4_mergesCaseDuplicatesAndAddsUniqueIndex() {
        helper.createDatabase(TEST_DB, 3).apply {
            execSQL("INSERT INTO word_lists (id, name, createdAt) VALUES (1, 'Animals', 0)")
            execSQL(
                """
                INSERT INTO words (id, listId, text, correctCount, incorrectCount, isMastered, masteredAt)
                VALUES (1, 1, 'Cat', 3, 1, 1, 100)
                """.trimIndent()
            )
            execSQL(
                """
                INSERT INTO words (id, listId, text, correctCount, incorrectCount, isMastered, masteredAt)
                VALUES (2, 1, 'cat', 0, 2, 0, NULL)
                """.trimIndent()
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 4, true, MIGRATION_3_4)

        db.query("SELECT id, text, correctCount, incorrectCount, isMastered, masteredAt FROM words").use { cursor ->
            assertThat(cursor.count).isEqualTo(1)
            cursor.moveToFirst()
            assertThat(cursor.getLong(0)).isEqualTo(1L)
            assertThat(cursor.getString(1)).isEqualTo("cat")
            assertThat(cursor.getInt(2)).isEqualTo(3)
            assertThat(cursor.getInt(3)).isEqualTo(3)
            assertThat(cursor.getInt(4)).isEqualTo(1)
            assertThat(cursor.getLong(5)).isEqualTo(100L)
        }

        db.query(
            "SELECT name FROM sqlite_master WHERE type = 'index' AND name = 'index_words_listId_text'"
        ).use { cursor ->
            assertThat(cursor.count).isEqualTo(1)
        }

        db.close()
    }

    private companion object {
        const val TEST_DB = "migration-test"
    }
}
