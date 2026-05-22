package com.itclimb.spellcoach.domain.practice

/**
 * Character-level spelling feedback using optimal Levenshtein alignment
 * (insertions, deletions, substitutions). The user's attempt is never rewritten.
 */
data class SpellingFeedback(
    val isCorrect: Boolean,
    val message: String,
    val attempt: String,
    val correctWord: String,
    val displayUnits: List<SpellingDisplayUnit>,
    val missingLetters: List<Char>,
    val extraLetters: List<Char>,
    val wrongSubstitutions: List<WrongSubstitution>
)

sealed interface SpellingDisplayUnit {
    data class Letter(val char: Char, val kind: SpellingLetterKind) : SpellingDisplayUnit
    data class Missing(val expected: Char) : SpellingDisplayUnit
}

enum class SpellingLetterKind {
    Correct,
    WrongSubstitution,
    Extra
}

data class WrongSubstitution(val attempt: Char, val expected: Char)

private sealed interface DiffOp {
    data class Match(val char: Char) : DiffOp
    data class Substitute(val attempt: Char, val correct: Char) : DiffOp
    data class Missing(val correct: Char) : DiffOp
    data class Extra(val attempt: Char) : DiffOp
}

object SpellingComparer {
    private const val CORRECT_MESSAGE = "Correct!"

    fun compare(attempt: String, correctWord: String): SpellingFeedback {
        val user = attempt.trim()
        val target = correctWord.trim()

        if (user.equals(target, ignoreCase = true)) {
            return SpellingFeedback(
                isCorrect = true,
                message = CORRECT_MESSAGE,
                attempt = user,
                correctWord = target,
                displayUnits = user.map { ch ->
                    SpellingDisplayUnit.Letter(ch, SpellingLetterKind.Correct)
                },
                missingLetters = emptyList(),
                extraLetters = emptyList(),
                wrongSubstitutions = emptyList()
            )
        }

        val ops = align(user, target)
        val displayUnits = buildDisplayUnits(ops)
        val missing = ops.filterIsInstance<DiffOp.Missing>().map { it.correct }
        val extras = ops.filterIsInstance<DiffOp.Extra>().map { it.attempt }
        val substitutions = ops.filterIsInstance<DiffOp.Substitute>().map {
            WrongSubstitution(attempt = it.attempt, expected = it.correct)
        }

        return SpellingFeedback(
            isCorrect = false,
            message = "",
            attempt = user,
            correctWord = target,
            displayUnits = displayUnits,
            missingLetters = missing,
            extraLetters = extras,
            wrongSubstitutions = substitutions
        )
    }

    private fun buildDisplayUnits(ops: List<DiffOp>): List<SpellingDisplayUnit> =
        ops.map { op ->
            when (op) {
                is DiffOp.Match -> SpellingDisplayUnit.Letter(op.char, SpellingLetterKind.Correct)
                is DiffOp.Substitute -> SpellingDisplayUnit.Letter(
                    op.attempt,
                    SpellingLetterKind.WrongSubstitution
                )
                is DiffOp.Extra -> SpellingDisplayUnit.Letter(op.attempt, SpellingLetterKind.Extra)
                is DiffOp.Missing -> SpellingDisplayUnit.Missing(op.correct)
            }
        }

    private fun align(attempt: String, correct: String): List<DiffOp> {
        val a = attempt
        val b = correct
        val n = a.length
        val m = b.length

        if (n == 0 && m == 0) return emptyList()

        val dp = Array(n + 1) { IntArray(m + 1) }
        for (i in 0..n) dp[i][0] = i
        for (j in 0..m) dp[0][j] = j

        for (i in 1..n) {
            for (j in 1..m) {
                val substitutionCost =
                    if (a[i - 1].equals(b[j - 1], ignoreCase = true)) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + substitutionCost
                )
            }
        }

        val ops = mutableListOf<DiffOp>()
        var i = n
        var j = m
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && a[i - 1].equals(b[j - 1], ignoreCase = true)) {
                ops.add(DiffOp.Match(a[i - 1]))
                i--
                j--
                continue
            }

            val canSubstitute =
                i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1
            val canDeleteExtra =
                i > 0 && dp[i][j] == dp[i - 1][j] + 1
            val canInsertMissing =
                j > 0 && dp[i][j] == dp[i][j - 1] + 1

            when {
                canSubstitute -> {
                    ops.add(DiffOp.Substitute(a[i - 1], b[j - 1]))
                    i--
                    j--
                }
                canDeleteExtra -> {
                    ops.add(DiffOp.Extra(a[i - 1]))
                    i--
                }
                canInsertMissing -> {
                    ops.add(DiffOp.Missing(b[j - 1]))
                    j--
                }
            }
        }

        return ops.asReversed()
    }
}
