package com.itclimb.spellcoach.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.itclimb.spellcoach.domain.word.WordTextNormalizer

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val rows = mutableListOf<MigrationWordRow>()
        db.query("SELECT id, listId, text, correctCount, incorrectCount, isMastered, masteredAt FROM words").use { cursor ->
            while (cursor.moveToNext()) {
                rows.add(
                    MigrationWordRow(
                        id = cursor.getLong(0),
                        listId = cursor.getLong(1),
                        text = cursor.getString(2),
                        correctCount = cursor.getInt(3),
                        incorrectCount = cursor.getInt(4),
                        isMastered = cursor.getInt(5) != 0,
                        masteredAt = if (cursor.isNull(6)) null else cursor.getLong(6),
                    )
                )
            }
        }

        for (row in rows) {
            if (WordTextNormalizer.canonicalizeForMigration(row.text) == null) {
                db.execSQL("DELETE FROM words WHERE id = ?", arrayOf(row.id))
            }
        }

        val migratable = rows.filter { WordTextNormalizer.canonicalizeForMigration(it.text) != null }

        for (merged in WordMigrationSupport.mergeRows(migratable)) {
            for (deleteId in merged.deleteIds) {
                db.execSQL("DELETE FROM words WHERE id = ?", arrayOf(deleteId))
            }
            db.execSQL(
                """
                UPDATE words
                SET text = ?, correctCount = ?, incorrectCount = ?, isMastered = ?, masteredAt = ?
                WHERE id = ?
                """.trimIndent(),
                arrayOf<Any?>(
                    merged.text,
                    merged.correctCount,
                    merged.incorrectCount,
                    if (merged.isMastered) 1 else 0,
                    merged.masteredAt,
                    merged.keeperId,
                ),
            )
        }

        db.execSQL("DROP INDEX IF EXISTS `index_words_listId`")
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_words_listId_text` ON `words` (`listId`, `text`)"
        )
    }
}
