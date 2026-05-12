package com.example.spellcoach.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spellcoach.core.designsystem.theme.SpellCoachThemeExtras
import com.example.spellcoach.core.designsystem.tokens.AppDimensions
import com.example.spellcoach.core.designsystem.tokens.AppElevation
import com.example.spellcoach.core.designsystem.tokens.AppRadius
import com.example.spellcoach.core.designsystem.tokens.AppSpacing

@Composable
fun SpellCoachPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(AppRadius.lg)
    val fill = lerp(scheme.primary, scheme.primaryContainer, 0.22f)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimensions.buttonHeightDefault),
        shape = shape,
        contentPadding = PaddingValues(
            horizontal = AppSpacing.xl,
            vertical = AppSpacing.md
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = AppElevation.level2,
            pressedElevation = AppElevation.level1,
            disabledElevation = AppElevation.level0,
            hoveredElevation = AppElevation.level2,
            focusedElevation = AppElevation.level2
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = fill,
            contentColor = scheme.onPrimary,
            disabledContainerColor = scheme.primary.copy(alpha = 0.38f),
            disabledContentColor = scheme.onPrimary.copy(alpha = 0.65f)
        )
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(AppDimensions.topBarAvatar - 12.dp)
            )
            Spacer(Modifier.width(AppSpacing.sm + AppSpacing.xs))
        }
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val scheme = MaterialTheme.colorScheme
    val fill = if (containerColor == scheme.primary) {
        lerp(scheme.primary, scheme.primaryContainer, 0.22f)
    } else {
        lerp(containerColor, scheme.surface, 0.1f)
    }
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimensions.buttonHeightDefault),
        shape = RoundedCornerShape(AppRadius.lg),
        contentPadding = PaddingValues(
            horizontal = AppSpacing.xl,
            vertical = AppSpacing.md
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = AppElevation.level2,
            pressedElevation = AppElevation.level1,
            disabledElevation = AppElevation.level0,
            hoveredElevation = AppElevation.level2,
            focusedElevation = AppElevation.level2
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = fill,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun SpellCoachSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(AppRadius.lg)
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimensions.buttonHeightDefault),
        shape = shape,
        border = BorderStroke(
            width = 1.dp,
            color = scheme.outlineVariant.copy(alpha = 0.4f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = scheme.onSurface,
            disabledContentColor = scheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(AppDimensions.topBarAvatar - 12.dp)
            )
            Spacer(Modifier.width(AppSpacing.sm + AppSpacing.xs))
        }
        Text(
            text = text,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun SecondaryOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    SpellCoachSecondaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        leadingIcon = leadingIcon
    )
}

@Composable
fun SaveGreenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        containerColor = SpellCoachThemeExtras.current.positiveAction,
        contentColor = Color.White
    )
}
