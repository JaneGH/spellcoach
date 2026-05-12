package com.example.spellcoach.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.spellcoach.R
import com.example.spellcoach.core.designsystem.tokens.AppDimensions
import com.example.spellcoach.core.designsystem.tokens.AppSpacing

@Composable
fun SpellCoachTopBar(
    showBack: Boolean,
    onBack: () -> Unit,
    brandTitle: String,
    brandAccent: String? = null,
    screenTitle: String? = null,
    heroTitle: String? = null,
    subtitleBelowBrand: String? = null,
    profileInitials: String = "",
    modifier: Modifier = Modifier
) {
    val initials = profileInitials.ifBlank { stringResource(R.string.profile_initials_default) }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.xs, vertical = AppSpacing.sm + AppSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (showBack) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(
                    modifier = Modifier.padding(
                        start = if (showBack) AppSpacing.none else AppSpacing.md,
                        end = AppSpacing.md
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = brandTitle,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (brandAccent != null) {
                            Text(
                                text = "  $brandAccent",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = AppSpacing.xs)
                            )
                        }
                    }
                    if (screenTitle != null) {
                        Text(
                            text = screenTitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = AppSpacing.xs + AppSpacing.xs)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(AppDimensions.topBarAvatar)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        if (heroTitle != null) {
            Text(
                text = heroTitle,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    start = AppSpacing.lg,
                    end = AppSpacing.lg,
                    top = AppSpacing.sm
                )
            )
        }
        if (subtitleBelowBrand != null) {
            Text(
                text = subtitleBelowBrand,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    start = AppSpacing.lg,
                    end = AppSpacing.lg,
                    top = AppSpacing.sm + AppSpacing.xs,
                    bottom = AppSpacing.md
                )
            )
        }
    }
}
