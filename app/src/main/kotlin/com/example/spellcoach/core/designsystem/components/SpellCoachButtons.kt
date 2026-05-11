package com.example.spellcoach.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spellcoach.core.designsystem.theme.SpellCoachThemeExtras
import com.example.spellcoach.core.designsystem.tokens.AppDimensions
import com.example.spellcoach.core.designsystem.tokens.AppRadius

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimensions.buttonHeightDefault),
        shape = RoundedCornerShape(AppRadius.sm),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun SecondaryOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimensions.buttonHeightDefault),
        shape = RoundedCornerShape(AppRadius.sm),
        border = BorderStroke(width = 1.dp, color = primary),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = primary)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
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
