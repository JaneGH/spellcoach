package com.example.spellcoach.core.designsystem.tokens

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppDimensions {
    val minTouchTarget: Dp = 48.dp
    val buttonHeightDefault: Dp = 48.dp
    val buttonHeightLarge: Dp = 52.dp
    val bottomBarHeightMin: Dp = 56.dp
    val topBarAvatar: Dp = 40.dp
    val settingsBannerHeight: Dp = 160.dp
    val handwritingPanelMinHeight: Dp = 320.dp
    val resultsHeroImage: Dp = 150.dp
    val addWordsFieldHeight: Dp = 160.dp
    val progressBarTrackHeight: Dp = 8.dp
    /** Premium slim track used by [SpellCoachProgressBar] for cards and inline progress. */
    val progressBarTrackSlim: Dp = 6.dp
    val letterChipMinSize: Dp = 52.dp
    val handwritingSubmitWidth: Dp = 120.dp
    val handwritingSubmitHeight: Dp = 44.dp

    /** Practice “listen” control: slightly smaller orb keeps the speaker glyph visually balanced. */
    val practiceSpeakerOrb: Dp = 56.dp
    val practiceSpeakerIcon: Dp = 30.dp
}
