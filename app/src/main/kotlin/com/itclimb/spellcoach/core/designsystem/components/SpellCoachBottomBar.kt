package com.itclimb.spellcoach.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.core.designsystem.tokens.AppBorder
import com.itclimb.spellcoach.core.designsystem.tokens.AppDimensions
import com.itclimb.spellcoach.core.designsystem.tokens.AppIconSize
import com.itclimb.spellcoach.core.designsystem.tokens.AppRadius
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing
import com.itclimb.spellcoach.core.designsystem.tokens.AppElevation

enum class MainTab { Lists, Practice, Settings }

@Composable
fun SpellCoachBottomBar(
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val isLight = scheme.background.luminance() > 0.5f
    val barShape = RoundedCornerShape(AppRadius.xxxl)
    val barFill = lerp(
        scheme.surfaceContainerHigh,
        scheme.surface,
        if (isLight) 0.35f else 0.28f
    )
    val barBorder = scheme.outlineVariant.copy(alpha = if (isLight) 0.14f else 0.22f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm + AppSpacing.xs)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = AppElevation.level3,
                    shape = barShape,
                    ambientColor = scheme.primary.copy(alpha = 0.06f),
                    spotColor = scheme.primary.copy(alpha = 0.10f)
                )
                .clip(barShape)
                .background(barFill.copy(alpha = if (isLight) 0.94f else 0.92f))
                .border(AppBorder.hairline, barBorder, barShape)
                .padding(vertical = AppSpacing.xs + AppSpacing.xxs, horizontal = AppSpacing.sm),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpellCoachNavItem(
                label = stringResource(R.string.nav_lists),
                selected = selected == MainTab.Lists,
                onClick = { onSelect(MainTab.Lists) },
                icon = Icons.AutoMirrored.Filled.List
            )
            SpellCoachNavItem(
                label = stringResource(R.string.nav_practice),
                selected = selected == MainTab.Practice,
                onClick = { onSelect(MainTab.Practice) },
                icon = Icons.Outlined.EditNote
            )
            SpellCoachNavItem(
                label = stringResource(R.string.nav_settings),
                selected = selected == MainTab.Settings,
                onClick = { onSelect(MainTab.Settings) },
                icon = Icons.Filled.Settings
            )
        }
    }
}

@Composable
private fun SpellCoachNavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector
) {
    val scheme = MaterialTheme.colorScheme
    val isLight = scheme.background.luminance() > 0.5f
    val selectedProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "nav_selected"
    )
    val bg = lerp(
        Color.Transparent,
        scheme.primary.copy(alpha = if (isLight) 0.10f else 0.18f),
        selectedProgress
    )
    val iconTint = lerp(
        scheme.onSurfaceVariant.copy(alpha = 0.78f),
        scheme.primary,
        selectedProgress
    )
    val labelColor = lerp(
        scheme.onSurfaceVariant.copy(alpha = 0.82f),
        scheme.primary,
        selectedProgress
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "nav_icon_scale"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .sizeIn(
                minWidth = AppDimensions.minTouchTarget,
                minHeight = AppDimensions.minTouchTarget
            )
            .clip(RoundedCornerShape(AppRadius.md))
            .semantics { role = Role.Button }
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs + AppSpacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier
                .size(AppIconSize.xl)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
        )
        Text(
            text = label,
            color = labelColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
