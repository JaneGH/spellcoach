package com.itclimb.spellcoach.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import androidx.compose.animation.core.animateFloatAsState
import com.itclimb.spellcoach.core.designsystem.motion.SpellCoachMotion
import com.itclimb.spellcoach.core.designsystem.tokens.AppElevation
import com.itclimb.spellcoach.core.designsystem.tokens.AppRadius

@Composable
fun SpellCoachOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    height: Dp? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 12,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(AppRadius.lg)
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val focusLift by animateFloatAsState(
        targetValue = if (focused) 1f else 0f,
        animationSpec = SpellCoachMotion.gentleSpring(),
        label = "field_focus_lift"
    )

    val fieldModifier = if (height != null) {
        modifier
            .fillMaxWidth()
            .height(height)
    } else {
        modifier.fillMaxWidth()
    }

    val borderFocused = scheme.primary.copy(alpha = 0.42f + 0.12f * focusLift)
    val shadowElev = lerp(AppElevation.level1, AppElevation.level2, focusLift)

    Box(
        modifier = fieldModifier
            .shadow(
                elevation = shadowElev,
                shape = shape,
                ambientColor = scheme.primary.copy(alpha = 0.04f + 0.08f * focusLift),
                spotColor = scheme.primary.copy(alpha = 0.06f + 0.10f * focusLift)
            )
            .clip(shape)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = scheme.onSurfaceVariant.copy(alpha = 0.44f)
                )
            },
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            shape = shape,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderFocused,
                unfocusedBorderColor = scheme.outlineVariant.copy(alpha = 0.32f),
                disabledBorderColor = scheme.outlineVariant.copy(alpha = 0.22f),
                cursorColor = scheme.primary,
                focusedTextColor = scheme.onSurface,
                unfocusedTextColor = scheme.onSurface,
                focusedContainerColor = scheme.surface,
                unfocusedContainerColor = scheme.surfaceContainerLow.copy(alpha = 0.55f),
                disabledContainerColor = scheme.surfaceVariant.copy(alpha = 0.24f)
            )
        )
    }
}

/** Alias for [SpellCoachOutlinedTextField] — same styling and behavior. */
@Composable
fun SpellCoachTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    height: Dp? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 12,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    SpellCoachOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        height = height,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation
    )
}
