package com.itclimb.spellcoach.data.practice

import com.itclimb.spellcoach.domain.model.PracticeResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeResultCache @Inject constructor() {
    @Volatile
    private var last: PracticeResult? = null

    fun set(result: PracticeResult) {
        last = result
    }

    fun consume(): PracticeResult? {
        val r = last
        last = null
        return r
    }

    fun peek(): PracticeResult? = last
}
