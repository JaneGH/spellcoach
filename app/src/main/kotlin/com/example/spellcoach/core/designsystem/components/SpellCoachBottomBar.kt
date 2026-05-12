package com.example.spellcoach.core.designsystem.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spellcoach.R
import com.example.spellcoach.core.designsystem.tokens.AppDimensions
import com.example.spellcoach.core.designsystem.tokens.AppRadius
import com.example.spellcoach.core.designsystem.tokens.AppSpacing

enum class MainTab { Lists, Practice, Settings }

@Composable
fun SpellCoachBottomBar(
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val isLight = scheme.background.luminance() > 0.5f
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                lerp(
                    scheme.surface,
                    scheme.background,
                    if (isLight) 0.1f else 0.06f
                )
            )
    ) {
        HorizontalDivider(color = scheme.outlineVariant.copy(alpha = 0.22f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = AppSpacing.xs + AppSpacing.xs, horizontal = AppSpacing.sm),
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
    val bg = if (selected) {
        scheme.primary.copy(alpha = if (isLight) 0.07f else 0.13f)
    } else {
        Color.Transparent
    }
    val tint = if (selected) {
        scheme.primary
    } else {
        scheme.onSurfaceVariant.copy(alpha = 0.88f)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .sizeIn(
                minWidth = AppDimensions.minTouchTarget,
                minHeight = AppDimensions.minTouchTarget
            )
            .clip(RoundedCornerShape(AppRadius.sm))
            .semantics { role = Role.Button }
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.sm + AppSpacing.xs, vertical = AppSpacing.xs + AppSpacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = tint,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
