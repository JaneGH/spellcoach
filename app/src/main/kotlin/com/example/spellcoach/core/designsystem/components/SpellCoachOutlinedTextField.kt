package com.example.spellcoach.core.designsystem.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.example.spellcoach.core.designsystem.tokens.AppRadius

@Composable
fun SpellCoachOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    height: Dp? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(AppRadius.md)
    val fieldModifier = if (height != null) {
        modifier
            .fillMaxWidth()
            .height(height)
    } else {
        modifier.fillMaxWidth()
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = fieldModifier,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = scheme.onSurfaceVariant.copy(alpha = 0.55f)
            )
        },
        singleLine = singleLine,
        shape = shape,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = scheme.primary,
            unfocusedBorderColor = scheme.outlineVariant,
            cursorColor = scheme.primary,
            focusedTextColor = scheme.onSurface,
            unfocusedTextColor = scheme.onSurface,
            focusedContainerColor = scheme.surface,
            unfocusedContainerColor = scheme.surface,
            disabledContainerColor = scheme.surface
        )
    )
}
