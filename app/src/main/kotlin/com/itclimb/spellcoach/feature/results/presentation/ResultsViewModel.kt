package com.itclimb.spellcoach.feature.results.presentation

import androidx.lifecycle.ViewModel
import com.itclimb.spellcoach.domain.practice.PracticeResultBuffer
import com.itclimb.spellcoach.domain.model.PracticeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    practiceResultBuffer: PracticeResultBuffer
) : ViewModel() {
    val result: PracticeResult? = practiceResultBuffer.consume()
}
