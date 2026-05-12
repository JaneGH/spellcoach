package com.itclimb.spellcoach.core.designsystem.components

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
import androidx.compose.ui.unit.sp
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing

enum class SpellCoachTopBarVariant {
    /** App brand + supportive subtitle; top-level tab roots only. */
    BrandedRoot,

    /** Back control + single navigation title; no app brand. */
    Inner,
}

@Composable
fun SpellCoachTopBar(
    modifier: Modifier = Modifier,
    variant: SpellCoachTopBarVariant,
    onBack: () -> Unit = {},
    rootSubtitle: String? = null,
    innerTitle: String? = null,
    innerCaption: String? = null,
) {
    when (variant) {
        SpellCoachTopBarVariant.BrandedRoot -> {
            rootSubtitle?.let { subtitle ->
                val brandStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 23.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.15).sp
                )
                val subtitleStyle = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.1.sp
                )
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(
                            start = AppSpacing.md,
                            end = AppSpacing.md,
                            top = AppSpacing.md,
                            bottom = AppSpacing.xxl + AppSpacing.sm
                        )
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = brandStyle
                    )
                    Text(
                        text = subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f),
                        style = subtitleStyle,
                        modifier = Modifier.padding(top = AppSpacing.xxs + AppSpacing.xs)
                    )
                }
            }
        }

        SpellCoachTopBarVariant.Inner -> {
            innerTitle?.let { title ->
                val titleStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 27.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.18).sp
                )
                val captionStyle = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.08.sp
                )
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(
                            start = AppSpacing.xs,
                            end = AppSpacing.sm,
                            top = AppSpacing.sm + AppSpacing.xs,
                            bottom = AppSpacing.lg + AppSpacing.sm
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = AppSpacing.md)
                    ) {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = titleStyle
                        )
                        if (innerCaption != null) {
                            Text(
                                text = innerCaption,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f),
                                style = captionStyle,
                                modifier = Modifier.padding(top = AppSpacing.xs)
                            )
                        }
                    }
                }
            }
        }
    }
}
