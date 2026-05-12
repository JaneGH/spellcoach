package com.itclimb.spellcoach.data.practice

import com.itclimb.spellcoach.domain.model.PracticeResult
import com.itclimb.spellcoach.domain.practice.PracticeResultBuffer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeResultCache @Inject constructor() : PracticeResultBuffer {
    @Volatile
    private var last: PracticeResult? = null

    override fun set(result: PracticeResult) {
        last = result
    }

    override fun consume(): PracticeResult? {
        val r = last
        last = null
        return r
    }

    override fun peek(): PracticeResult? = last
}
