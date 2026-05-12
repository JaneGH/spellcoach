package com.itclimb.spellcoach.core.designsystem.tokens

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppSpacing {
    val none: Dp = 0.dp
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp

    /** Horizontal inset for screen content below the top bar. */
    val screenHorizontal: Dp = lg

    /** Inner padding for elevated cards ([SpellCoachCard], [LearningCard]). */
    val cardContentPadding: Dp = lg
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val sheetBottom: Dp = 88.dp
    val fabClearance: Dp = 90.dp

    /** Vertical gap between cards on scroll screens (Lists, Settings). */
    val sectionGap: Dp = sm + md

    /** FAB distance from the bottom of the screen; clears bottom nav without sitting too low. */
    val fabBottomInset: Dp = xxl + sm
}
