package com.example.spellcoach.presentation.results

import androidx.lifecycle.ViewModel
import com.example.spellcoach.data.practice.PracticeResultCache
import com.example.spellcoach.domain.model.PracticeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    practiceResultCache: PracticeResultCache
) : ViewModel() {
    val result: PracticeResult? = practiceResultCache.consume()
}
