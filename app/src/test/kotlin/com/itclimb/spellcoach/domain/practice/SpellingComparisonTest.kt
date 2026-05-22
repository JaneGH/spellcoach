package com.itclimb.spellcoach.domain.practice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpellingComparisonTest {

    @Test
    fun correctAnswer_isMarkedCorrect() {
        val result = SpellingComparer.compare("Apple", "apple")
        assertTrue(result.isCorrect)
        assertEquals("Correct!", result.message)
        assertEquals(5, result.displayUnits.size)
        assertTrue(result.displayUnits.all { it is SpellingDisplayUnit.Letter })
    }

    @Test
    fun aplle_vs_apple_highlightsWrongLetter_notExtra() {
        val result = SpellingComparer.compare("aplle", "apple")
        assertFalse(result.isCorrect)
        assertTrue(result.extraLetters.isEmpty())
        assertEquals('l', result.wrongSubstitutions.single().attempt)
        assertEquals('p', result.wrongSubstitutions.single().expected)
        val kinds = result.displayUnits.filterIsInstance<SpellingDisplayUnit.Letter>().map { it.kind }
        assertEquals(
            listOf(
                SpellingLetterKind.Correct,
                SpellingLetterKind.Correct,
                SpellingLetterKind.WrongSubstitution,
                SpellingLetterKind.Correct,
                SpellingLetterKind.Correct
            ),
            kinds
        )
    }

    @Test
    fun appple_vs_apple_highlightsExtraLetter() {
        val result = SpellingComparer.compare("appple", "apple")
        assertFalse(result.isCorrect)
        assertEquals(listOf('p'), result.extraLetters)
        assertTrue(result.displayUnits.any {
            it is SpellingDisplayUnit.Letter && it.kind == SpellingLetterKind.Extra
        })
    }

    @Test
    fun becouse_vs_because_highlightsWrongLetter() {
        val result = SpellingComparer.compare("becouse", "because")
        assertFalse(result.isCorrect)
        assertEquals(1, result.wrongSubstitutions.size)
        assertEquals('o', result.wrongSubstitutions.single().attempt)
        assertEquals('a', result.wrongSubstitutions.single().expected)
    }

    @Test
    fun enviroment_vs_environment_marksMissingSeparately() {
        val result = SpellingComparer.compare("enviroment", "environment")
        assertFalse(result.isCorrect)
        assertEquals(listOf('n'), result.missingLetters)
        assertTrue(result.displayUnits.any { it is SpellingDisplayUnit.Missing })
        val attemptLetters = result.displayUnits
            .filterIsInstance<SpellingDisplayUnit.Letter>()
            .joinToString("") { it.char.toString() }
        assertEquals("enviroment", attemptLetters)
    }
}
