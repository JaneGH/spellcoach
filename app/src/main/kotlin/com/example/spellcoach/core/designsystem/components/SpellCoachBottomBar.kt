package com.example.spellcoach.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(scheme.surfaceContainerLow)
    ) {
        HorizontalDivider(color = scheme.outlineVariant.copy(alpha = 0.35f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = AppSpacing.sm + AppSpacing.xs, horizontal = AppSpacing.md),
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
    val bg = if (selected) {
        scheme.primaryContainer.copy(alpha = 0.72f)
    } else {
        scheme.surfaceContainerLow.copy(alpha = 0f)
    }
    val tint = if (selected) scheme.onPrimaryContainer else scheme.onSurfaceVariant
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .sizeIn(
                minWidth = AppDimensions.minTouchTarget,
                minHeight = AppDimensions.minTouchTarget
            )
            .clip(RoundedCornerShape(AppRadius.xxl))
            .semantics { role = Role.Button }
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier
                .padding(vertical = AppSpacing.xxs)
        )
        Text(
            text = label,
            color = tint,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
