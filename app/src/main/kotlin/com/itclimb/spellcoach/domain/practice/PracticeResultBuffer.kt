package com.itclimb.spellcoach.domain.practice

import com.itclimb.spellcoach.domain.model.PracticeResult

interface PracticeResultBuffer {
    fun set(result: PracticeResult)
    fun consume(): PracticeResult?
    fun peek(): PracticeResult?
}
