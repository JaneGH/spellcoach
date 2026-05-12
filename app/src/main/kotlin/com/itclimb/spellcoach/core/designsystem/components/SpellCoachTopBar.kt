package com.itclimb.spellcoach.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing

@Composable
fun SpellCoachTopBar(
    modifier: Modifier = Modifier,
    showBack: Boolean,
    onBack: () -> Unit,
    brandTitle: String,
    brandAccent: String? = null,
    screenTitle: String? = null,
    heroTitle: String? = null,
    subtitleBelowBrand: String? = null,
    profileInitials: String = ""
) {
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
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (brandAccent != null) {
                            Text(
                                text = "  $brandAccent",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.86f),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = AppSpacing.xs)
                            )
                        }
                    }
                    if (screenTitle != null) {
                        Text(
                            text = screenTitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.88f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = AppSpacing.xxs + AppSpacing.xs)
                        )
                    }
                }
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
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.88f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    start = AppSpacing.lg,
                    end = AppSpacing.lg,
                    top = AppSpacing.xs + AppSpacing.xxs,
                    bottom = AppSpacing.md
                )
            )
        }
    }
}
