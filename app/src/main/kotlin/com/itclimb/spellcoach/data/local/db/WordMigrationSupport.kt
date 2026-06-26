package com.itclimb.spellcoach.data.local.db

import com.itclimb.spellcoach.domain.word.WordTextNormalizer

internal data class MigrationWordRow(
    val id: Long,
    val listId: Long,
    val text: String,
    val correctCount: Int,
    val incorrectCount: Int,
    val isMastered: Boolean,
    val masteredAt: Long?,
)

internal data class MergedMigrationWord(
    val keeperId: Long,
    val listId: Long,
    val text: String,
    val correctCount: Int,
    val incorrectCount: Int,
    val isMastered: Boolean,
    val masteredAt: Long?,
    val deleteIds: List<Long>,
)

internal object WordMigrationSupport {

    fun mergeRows(rows: List<MigrationWordRow>): List<MergedMigrationWord> =
        rows
            .mapNotNull { row ->
                WordTextNormalizer.canonicalizeForMigration(row.text)?.let { canonical -> row to canonical }
            }
            .groupBy { (row, canonical) -> row.listId to canonical }
            .map { (_, group) -> mergeGroup(group) }

    private fun mergeGroup(group: List<Pair<MigrationWordRow, String>>): MergedMigrationWord {
        val canonicalText = group.first().second
        val rows = group.map { it.first }
        val sorted = rows.sortedWith(
            compareByDescending<MigrationWordRow> { it.correctCount }
                .thenByDescending { it.isMastered }
                .thenBy { it.id }
        )
        val keeper = sorted.first()
        val others = sorted.drop(1)
        val mergedMastered = rows.any { it.isMastered }
        return MergedMigrationWord(
            keeperId = keeper.id,
            listId = keeper.listId,
            text = canonicalText,
            correctCount = rows.maxOf { it.correctCount },
            incorrectCount = rows.sumOf { it.incorrectCount },
            isMastered = mergedMastered,
            masteredAt = if (mergedMastered) rows.mapNotNull { it.masteredAt }.minOrNull() else null,
            deleteIds = others.map { it.id },
        )
    }
}
