package com.itclimb.spellcoach.core.designsystem.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itclimb.spellcoach.core.designsystem.tokens.AppBorder
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    height: Dp = 92.dp,
    cornerRadius: Dp = 24.dp,
    textStyle: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    ),
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        autoCorrectEnabled = false,
        capitalization = KeyboardCapitalization.None,
        keyboardType = KeyboardType.Ascii
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val shape = RoundedCornerShape(cornerRadius)
    val borderColor = Color(0xFF0F172A).copy(alpha = 0.10f)
    val fill = Color.White.copy(alpha = 0.62f)
    val placeholderColor = Color(0xFF64748B).copy(alpha = 0.40f)

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(fill)
            .border(AppBorder.hairline, borderColor, shape)
            .padding(horizontal = AppSpacing.xs + AppSpacing.xxs),
        textStyle = textStyle.merge(MaterialTheme.typography.bodyLarge),
        placeholder = {
            Text(
                text = placeholder,
                color = placeholderColor,
                fontSize = textStyle.fontSize,
                fontWeight = textStyle.fontWeight ?: FontWeight.Medium
            )
        },
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFF0284C7),
            focusedTextColor = Color(0xFF0F172A),
            unfocusedTextColor = Color(0xFF0F172A),
            focusedPlaceholderColor = placeholderColor,
            unfocusedPlaceholderColor = placeholderColor
        )
    )
}