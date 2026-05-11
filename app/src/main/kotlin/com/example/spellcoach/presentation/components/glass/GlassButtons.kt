package com.example.spellcoach.presentation.components.glass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    gradient: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFF0284C7), Color(0xFF06B6D4))
    )
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        label = "glass_button_scale"
    )

    val shape = RoundedCornerShape(999.dp)
    val borderColor = Color.White.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0A7DFF).copy(alpha = 0.86f),
                        Color(0xFF43D9FF).copy(alpha = 0.78f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.42f),
                shape = shape
            )
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = Color(0xFF38BDF8).copy(alpha = 0.25f),
                spotColor = Color(0xFF2563EB).copy(alpha = 0.22f)
            )
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp)
            .graphicsLayerScale(scale),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun GlassOrbIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 70.dp,
    enabled: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        label = "glass_orb_scale"
    )

    val shape = CircleShape
    val orbGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0EA5E9),
            Color(0xFF22D3EE)
        )
    )

    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 18.dp, shape = shape, clip = false)
            .clip(shape)
            .background(orbGradient)
            .border(1.dp, Color.White.copy(alpha = 0.45f), shape)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .graphicsLayerScale(scale),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}

private fun Modifier.graphicsLayerScale(scale: Float): Modifier =
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
