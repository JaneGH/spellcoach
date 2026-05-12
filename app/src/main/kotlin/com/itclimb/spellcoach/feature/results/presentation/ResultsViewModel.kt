package com.itclimb.spellcoach.feature.results.presentation

import androidx.lifecycle.ViewModel
import com.itclimb.spellcoach.data.practice.PracticeResultCache
import com.itclimb.spellcoach.domain.model.PracticeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    practiceResultCache: PracticeResultCache
) : ViewModel() {
    val result: PracticeResult? = practiceResultCache.consume()
}
