package com.itclimb.spellcoach.core.designsystem.tokens

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppDimensions {
    /** Keep in sync with `res/values/dimens.xml` `touch_target_min`. */
    val minTouchTarget: Dp = 48.dp
    val buttonHeightDefault: Dp = 48.dp
    val topBarAvatar: Dp = 40.dp
    val settingsBannerHeight: Dp = 138.dp
    /** Reserved width for settings toggle rows so titles and subtitles never overlap the switch. */
    val settingsToggleSlotWidth: Dp = 56.dp
    val handwritingPanelMinHeight: Dp = 320.dp
    /**
     * Results hero image: use `(maxWidth * resultsHeroImageWidthFraction).coerceIn(...)` with
     * [resultsHeroImageMin] / [resultsHeroImageMax].
     */
    val resultsHeroImageMin: Dp = 120.dp
    val resultsHeroImageMax: Dp = 200.dp
    const val resultsHeroImageWidthFraction: Float = 0.42f

    /**
     * Decorative mascots: multiply parent max width from [androidx.compose.foundation.layout.BoxWithConstraints]
     * by a screen-appropriate fraction, then clamp with the matching min/max pair.
     */
    val mascotInlineMin: Dp = 40.dp
    val mascotInlineMax: Dp = 56.dp
    const val mascotInlineWidthFraction: Float = 0.18f

    val mascotPromoMin: Dp = 80.dp
    val mascotPromoMax: Dp = 112.dp
    const val mascotPromoWidthFraction: Float = 0.35f

    val mascotSettingsBannerMin: Dp = 68.dp
    val mascotSettingsBannerMax: Dp = 100.dp
    const val mascotSettingsBannerWidthFraction: Float = 0.30f

    val mascotWrongCardMin: Dp = 64.dp
    val mascotWrongCardMax: Dp = 92.dp
    const val mascotWrongCardWidthFraction: Float = 0.30f

    val mascotCorrectCardMin: Dp = 80.dp
    val mascotCorrectCardMax: Dp = 112.dp
    const val mascotCorrectCardWidthFraction: Float = 0.28f

    val mascotCompletionDailyRingMin: Dp = 118.dp
    val mascotCompletionDailyRingMax: Dp = 152.dp
    const val mascotCompletionDailyRingWidthFraction: Float = 0.38f
    const val mascotCompletionDailyImageOfRing: Float = 118f / 132f

    val mascotCompletionMasteredRingMin: Dp = 152.dp
    val mascotCompletionMasteredRingMax: Dp = 200.dp
    const val mascotCompletionMasteredRingWidthFraction: Float = 0.48f
    const val mascotCompletionMasteredImageOfRing: Float = 155f / 175f
    val addWordsFieldHeight: Dp = 160.dp
    val addWordsListNameFieldHeight: Dp = 56.dp
    val addWordsAddButtonMinWidth: Dp = 160.dp
    val practiceKeyboardInputFieldHeight: Dp = 64.dp

    val progressBarTrackSlim: Dp = 6.dp
    val letterChipMinSize: Dp = 52.dp
    val handwritingSubmitWidth: Dp = 120.dp
    val handwritingSubmitHeight: Dp = 44.dp

    /** Practice “listen” control: slightly smaller orb keeps the speaker glyph visually balanced. */
    val practiceSpeakerOrb: Dp = 56.dp
    val practiceSpeakerIcon: Dp = 30.dp
}
